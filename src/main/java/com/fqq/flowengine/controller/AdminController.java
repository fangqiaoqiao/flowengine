package com.fqq.flowengine.controller;

import com.fqq.flowengine.model.dto.*;
import com.fqq.flowengine.model.entity.*;
import com.fqq.flowengine.repository.*;
import com.fqq.flowengine.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private ComponentService componentService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private ParamConfigService paramConfigService;
    @Autowired
    private ServiceDefService serviceDefService;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private NodeInputParamRepository inputParamRepository;
    @Autowired
    private NodeOutputParamRepository outputParamRepository;
    @Autowired
    private ServiceDefRepository serviceDefRepository;
    @Autowired
    private ServiceInputParamRepository serviceInputParamRepository;
    @Autowired
    private ServiceOutputParamRepository serviceOutputParamRepository;
    @Autowired
    private NodeInputMappingRepository mappingRepository;

    // ==================== 组件管理 ====================

    @GetMapping("/components")
    public ResponseEntity<List<ComponentDto>> listComponents() {
        List<Component> components = componentService.findAll();
        List<ComponentDto> dtos = components.stream()
                .map(this::convertToComponentDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/components/{componentId}")
    public ResponseEntity<ComponentDto> getComponent(@PathVariable String componentId) {
        Component component = componentService.findByComponentId(componentId);
        return ResponseEntity.ok(convertToComponentDto(component));
    }

    @PostMapping("/components")
    public ResponseEntity<ComponentDto> createComponent(@Valid @RequestBody ComponentDto dto) {
        Component component = new Component();
        component.setComponentId(dto.getComponentId());
        component.setName(dto.getName());
        component.setDescription(dto.getDescription());
        Component saved = componentService.save(component);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToComponentDto(saved));
    }

    @PutMapping("/components/{id}")
    public ResponseEntity<ComponentDto> updateComponent(@PathVariable Long id, @Valid @RequestBody ComponentDto dto) {
        Component existing = componentService.findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        Component updated = componentService.save(existing);
        return ResponseEntity.ok(convertToComponentDto(updated));
    }

    @DeleteMapping("/components/{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id) {
        componentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 环节管理 ====================

    @GetMapping("/components/{componentId}/nodes")
    public ResponseEntity<List<NodeDto>> listNodesByComponent(@PathVariable String componentId) {
        List<Node> nodes = nodeService.findByComponentIdOrderByNodeOrder(componentId);
        List<NodeDto> dtos = nodes.stream()
                .map(this::convertToNodeDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/nodes/{nodeId}")
    public ResponseEntity<NodeDto> getNode(@PathVariable String nodeId) {
        Node node = nodeService.findByNodeId(nodeId);
        return ResponseEntity.ok(convertToNodeDto(node));
    }

    @PostMapping("/nodes")
    public ResponseEntity<NodeDto> createNode(@Valid @RequestBody NodeDto dto) {
        Node node = new Node();
        node.setNodeId(dto.getNodeId());
        node.setComponentId(dto.getComponentId());
        node.setNodeName(dto.getNodeName());
        node.setNodeOrder(dto.getNodeOrder());
        node.setProcessorType(dto.getProcessorType());
        node.setProcessorConfig(dto.getProcessorConfig());
        node.setServiceId(dto.getServiceId());
        Node saved = nodeService.save(node);
        // 保存映射
        saveNodeMappings(saved.getNodeId(), dto.getInputMappings());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToNodeDto(saved));
    }

    @PutMapping("/nodes/{nodeId}")
    @Transactional   // 关键：确保删除和插入操作在事务内执行
    public ResponseEntity<NodeDto> updateNode(@PathVariable String nodeId, @Valid @RequestBody NodeDto dto) {
        logger.info("更新环节: nodeId={}, inputMappings数量={}", nodeId, dto.getInputMappings() == null ? 0 : dto.getInputMappings().size());

        Node existing = nodeService.findByNodeId(nodeId);
        existing.setNodeName(dto.getNodeName());
        existing.setNodeOrder(dto.getNodeOrder());
        existing.setProcessorType(dto.getProcessorType());
        existing.setProcessorConfig(dto.getProcessorConfig());
        existing.setServiceId(dto.getServiceId());
        Node updated = nodeService.save(existing);

        // 先删除旧映射
        mappingRepository.deleteByNodeId(nodeId);
        logger.info("已删除节点 {} 的旧映射", nodeId);

        // 再插入新映射
        if (dto.getInputMappings() != null && !dto.getInputMappings().isEmpty()) {
            for (NodeInputMappingDto mapDto : dto.getInputMappings()) {
                NodeInputMapping mapping = new NodeInputMapping();
                mapping.setNodeId(nodeId);
                mapping.setServiceInputParamId(mapDto.getServiceInputParamId());
                mapping.setSourceType(mapDto.getSourceType());
                mapping.setSourceValue(mapDto.getSourceValue());
                mappingRepository.save(mapping);
            }
            logger.info("已插入 {} 条新映射", dto.getInputMappings().size());
        }

        return ResponseEntity.ok(convertToNodeDto(updated));
    }

    @DeleteMapping("/nodes/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable String nodeId) {
        // 级联删除映射已在 Repository 层由 deleteByNodeId 处理
        nodeService.deleteByNodeId(nodeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nodes/{nodeId}/mappings")
    public ResponseEntity<List<NodeInputMappingDto>> getNodeMappings(@PathVariable String nodeId) {
        List<NodeInputMapping> mappings = mappingRepository.findByNodeId(nodeId);
        List<NodeInputMappingDto> dtos = mappings.stream()
                .map(this::convertToMappingDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== 入参配置管理 ====================

    @GetMapping("/nodes/{nodeId}/input-params")
    public ResponseEntity<List<ParamDto>> listInputParams(@PathVariable String nodeId) {
        List<NodeInputParam> params = paramConfigService.findInputParamsByNodeId(nodeId);
        List<ParamDto> dtos = params.stream()
                .map(this::convertToInputParamDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/input-params")
    public ResponseEntity<ParamDto> createInputParam(@Valid @RequestBody ParamDto dto) {
        NodeInputParam param = new NodeInputParam();
        param.setNodeId(dto.getNodeId());
        param.setParamName(dto.getParamName());
        param.setParamSource(dto.getParamSource());
        param.setSourceExpression(dto.getSourceExpression());
        param.setRequired(dto.getRequired());
        NodeInputParam saved = paramConfigService.saveInputParam(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToInputParamDto(saved));
    }

    @PutMapping("/input-params/{id}")
    public ResponseEntity<ParamDto> updateInputParam(@PathVariable Long id, @Valid @RequestBody ParamDto dto) {
        NodeInputParam existing = paramConfigService.findInputParamById(id);
        existing.setParamName(dto.getParamName());
        existing.setParamSource(dto.getParamSource());
        existing.setSourceExpression(dto.getSourceExpression());
        existing.setRequired(dto.getRequired());
        NodeInputParam updated = paramConfigService.saveInputParam(existing);
        return ResponseEntity.ok(convertToInputParamDto(updated));
    }

    @DeleteMapping("/input-params/{id}")
    public ResponseEntity<Void> deleteInputParam(@PathVariable Long id) {
        paramConfigService.deleteInputParam(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 出参配置管理 ====================

    @GetMapping("/nodes/{nodeId}/output-params")
    public ResponseEntity<List<ParamDto>> listOutputParams(@PathVariable String nodeId) {
        List<NodeOutputParam> params = paramConfigService.findOutputParamsByNodeId(nodeId);
        List<ParamDto> dtos = params.stream()
                .map(this::convertToOutputParamDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/output-params")
    public ResponseEntity<ParamDto> createOutputParam(@Valid @RequestBody ParamDto dto) {
        NodeOutputParam param = new NodeOutputParam();
        param.setNodeId(dto.getNodeId());
        param.setParamName(dto.getParamName());
        param.setParamValueExpression(dto.getParamValueExpression());
        NodeOutputParam saved = paramConfigService.saveOutputParam(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToOutputParamDto(saved));
    }

    @PutMapping("/output-params/{id}")
    public ResponseEntity<ParamDto> updateOutputParam(@PathVariable Long id, @Valid @RequestBody ParamDto dto) {
        NodeOutputParam existing = paramConfigService.findOutputParamById(id);
        existing.setParamName(dto.getParamName());
        existing.setParamValueExpression(dto.getParamValueExpression());
        NodeOutputParam updated = paramConfigService.saveOutputParam(existing);
        return ResponseEntity.ok(convertToOutputParamDto(updated));
    }

    @DeleteMapping("/output-params/{id}")
    public ResponseEntity<Void> deleteOutputParam(@PathVariable Long id) {
        paramConfigService.deleteOutputParam(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 服务管理 ====================

    @GetMapping("/services")
    public ResponseEntity<List<ServiceDefDto>> listServices() {
        List<ServiceDef> services = serviceDefService.findAll();
        List<ServiceDefDto> dtos = services.stream()
                .map(this::convertToServiceDefDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceDefDto> getService(@PathVariable Long id) {
        ServiceDef service = serviceDefService.findById(id);
        return ResponseEntity.ok(convertToServiceDefDto(service));
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceDefDto> createService(@Valid @RequestBody ServiceDefDto dto) {
        ServiceDef saved = serviceDefService.saveWithParams(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToServiceDefDto(saved));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceDefDto> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDefDto dto) {
        dto.setId(id);
        ServiceDef saved = serviceDefService.saveWithParams(dto);
        return ResponseEntity.ok(convertToServiceDefDto(saved));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceDefService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/services/{serviceId}/input-params")
    public ResponseEntity<List<ServiceInputParamDto>> getServiceInputParams(@PathVariable Long serviceId) {
        List<ServiceInputParam> params = serviceInputParamRepository.findByServiceIdOrderByIdAsc(serviceId);
        List<ServiceInputParamDto> dtos = params.stream()
                .map(this::convertToServiceInputParamDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/services/{serviceId}/output-params")
    public ResponseEntity<List<ServiceOutputParamDto>> getServiceOutputParams(@PathVariable Long serviceId) {
        List<ServiceOutputParam> params = serviceOutputParamRepository.findByServiceIdOrderByIdAsc(serviceId);
        List<ServiceOutputParamDto> dtos = params.stream()
                .map(this::convertToServiceOutputParamDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ==================== 私有辅助方法 ====================

    private void saveNodeMappings(String nodeId, List<NodeInputMappingDto> mappings) {
        if (mappings == null) return;
        for (NodeInputMappingDto mapDto : mappings) {
            NodeInputMapping mapping = new NodeInputMapping();
            mapping.setNodeId(nodeId);
            mapping.setServiceInputParamId(mapDto.getServiceInputParamId());
            mapping.setSourceType(mapDto.getSourceType());
            mapping.setSourceValue(mapDto.getSourceValue());
            mappingRepository.save(mapping);
        }
    }

    private ComponentDto convertToComponentDto(Component component) {
        ComponentDto dto = new ComponentDto();
        dto.setId(component.getId());
        dto.setComponentId(component.getComponentId());
        dto.setName(component.getName());
        dto.setDescription(component.getDescription());
        return dto;
    }

    private NodeDto convertToNodeDto(Node node) {
        NodeDto dto = new NodeDto();
        dto.setId(node.getId());
        dto.setNodeId(node.getNodeId());
        dto.setComponentId(node.getComponentId());
        dto.setNodeName(node.getNodeName());
        dto.setNodeOrder(node.getNodeOrder());
        dto.setProcessorType(node.getProcessorType());
        dto.setProcessorConfig(node.getProcessorConfig());
        dto.setServiceId(node.getServiceId());
        // 加载映射
        List<NodeInputMapping> mappings = mappingRepository.findByNodeId(node.getNodeId());
        dto.setInputMappings(mappings.stream().map(this::convertToMappingDto).collect(Collectors.toList()));
        return dto;
    }

    private NodeInputMappingDto convertToMappingDto(NodeInputMapping mapping) {
        NodeInputMappingDto dto = new NodeInputMappingDto();
        dto.setId(mapping.getId());
        dto.setNodeId(mapping.getNodeId());
        dto.setServiceInputParamId(mapping.getServiceInputParamId());
        dto.setSourceType(mapping.getSourceType());
        dto.setSourceValue(mapping.getSourceValue());
        return dto;
    }

    private ParamDto convertToInputParamDto(NodeInputParam param) {
        ParamDto dto = new ParamDto();
        dto.setId(param.getId());
        dto.setNodeId(param.getNodeId());
        dto.setParamName(param.getParamName());
        dto.setParamSource(param.getParamSource());
        dto.setSourceExpression(param.getSourceExpression());
        dto.setRequired(param.getRequired());
        return dto;
    }

    private ParamDto convertToOutputParamDto(NodeOutputParam param) {
        ParamDto dto = new ParamDto();
        dto.setId(param.getId());
        dto.setNodeId(param.getNodeId());
        dto.setParamName(param.getParamName());
        dto.setParamValueExpression(param.getParamValueExpression());
        dto.setParamSource("output");
        dto.setRequired(false);
        return dto;
    }

    private ServiceDefDto convertToServiceDefDto(ServiceDef service) {
        ServiceDefDto dto = new ServiceDefDto();
        dto.setId(service.getId());
        dto.setServiceCode(service.getServiceCode());
        dto.setServiceName(service.getServiceName());
        dto.setDescription(service.getDescription());
        dto.setProtocol(service.getProtocol());
        dto.setUrl(service.getUrl());
        dto.setMethod(service.getMethod());
        dto.setHeaders(service.getHeaders());
        // 加载入参出参
        List<ServiceInputParam> inputs = serviceInputParamRepository.findByServiceIdOrderByIdAsc(service.getId());
        dto.setInputParams(inputs.stream().map(this::convertToServiceInputParamDto).collect(Collectors.toList()));
        List<ServiceOutputParam> outputs = serviceOutputParamRepository.findByServiceIdOrderByIdAsc(service.getId());
        dto.setOutputParams(outputs.stream().map(this::convertToServiceOutputParamDto).collect(Collectors.toList()));
        return dto;
    }

    private ServiceInputParamDto convertToServiceInputParamDto(ServiceInputParam param) {
        ServiceInputParamDto dto = new ServiceInputParamDto();
        dto.setId(param.getId());
        dto.setServiceId(param.getServiceId());
        dto.setParamName(param.getParamName());
        dto.setParamType(param.getParamType());
        dto.setRequired(param.getRequired());
        dto.setDescription(param.getDescription());
        return dto;
    }

    private ServiceOutputParamDto convertToServiceOutputParamDto(ServiceOutputParam param) {
        ServiceOutputParamDto dto = new ServiceOutputParamDto();
        dto.setId(param.getId());
        dto.setServiceId(param.getServiceId());
        dto.setParamName(param.getParamName());
        dto.setParamType(param.getParamType());
        dto.setDescription(param.getDescription());
        return dto;
    }
}
package com.fqq.flowengine.model.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 环节数据传输对象
 */
public class NodeDto {

    private Long id;

    @NotBlank(message = "nodeId cannot be blank")
    private String nodeId;

    private Long serviceId; // 关联服务ID
    private List<NodeInputMappingDto> inputMappings; // 环节服务入参映射

    @NotBlank(message = "componentId cannot be blank")
    private String componentId;

    @NotBlank(message = "nodeName cannot be blank")
    private String nodeName;

    @NotNull(message = "nodeOrder cannot be null")
    private Integer nodeOrder;

    @NotBlank(message = "processorType cannot be blank")
    private String processorType; // http, script, dummy

    private String processorConfig; // 可为空

    public NodeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public List<NodeInputMappingDto> getInputMappings() {
        return inputMappings;
    }

    public void setInputMappings(List<NodeInputMappingDto> inputMappings) {
        this.inputMappings = inputMappings;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public String getProcessorConfig() {
        return processorConfig;
    }

    public void setProcessorConfig(String processorConfig) {
        this.processorConfig = processorConfig;
    }
}
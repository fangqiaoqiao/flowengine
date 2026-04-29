package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.model.entity.*;
import com.fqq.flowengine.processor.NodeProcessor;
import com.fqq.flowengine.processor.NodeProcessorFactory;
import com.fqq.flowengine.repository.*;
import com.fqq.flowengine.service.*;
import com.fqq.flowengine.util.ExpressionEvaluator;
import com.fqq.flowengine.util.ServiceInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowEngineServiceImpl implements FlowEngineService {

    private static final Logger logger = LoggerFactory.getLogger(FlowEngineServiceImpl.class);

    @Autowired
    private NodeService nodeService;
    @Autowired
    private ParamConfigService paramConfigService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private NodeProcessorFactory processorFactory;
    @Autowired
    private ExpressionEvaluator expressionEvaluator;

    // 服务调用相关依赖
    @Autowired
    private ServiceDefService serviceDefService;
    @Autowired
    private ServiceInputParamRepository serviceInputParamRepository;
    @Autowired
    private ServiceOutputParamRepository serviceOutputParamRepository;
    @Autowired
    private NodeInputMappingRepository mappingRepository;
    @Autowired
    private ServiceInvoker serviceInvoker;

    @Override
    public String getFirstNodeId(String componentId) {
        return nodeService.getFirstNodeId(componentId);
    }

    @Override
    public Map<String, Object> executeNode(String componentId, String nodeId,
                                            Map<String, Map<String, Object>> context) {
        // 1. 获取环节配置
        Node node = nodeService.findByNodeId(nodeId);
        if (!componentId.equals(node.getComponentId())) {
            throw new IllegalArgumentException("环节 " + nodeId + " 不属于组件 " + componentId);
        }

        // 2. 如果环节关联了服务，走服务调用逻辑
        if (node.getServiceId() != null && node.getServiceId() > 0) {
            return executeServiceNode(node, context);
        }

        // 3. 否则走原有处理器逻辑
        return executeProcessorNode(node, context);
    }

    /**
     * 执行处理器环节（原有逻辑）
     */
    private Map<String, Object> executeProcessorNode(Node node, Map<String, Map<String, Object>> context) {
        String nodeId = node.getNodeId();

        // 获取入参配置
        List<NodeInputParam> inputParams = paramConfigService.findInputParamsByNodeId(nodeId);
        // 解析入参
        Map<String, Object> resolvedInput = resolveInputs(inputParams, context, nodeId);

        // 获取处理器并执行
        NodeProcessor processor = processorFactory.getProcessor(node.getProcessorType());
        Map<String, Object> rawOutput = processor.process(resolvedInput, node.getProcessorConfig());

        // 获取出参映射配置并提取输出字段
        List<NodeOutputParam> outputParams = paramConfigService.findOutputParamsByNodeId(nodeId);
        Map<String, Object> filteredOutput = extractOutputs(outputParams, rawOutput);

        // 存储输出到上下文
        context.put(nodeId + "_out", filteredOutput);
        if (!context.containsKey(nodeId)) {
            context.put(nodeId, new HashMap<>());
        }

        return filteredOutput;
    }

    /**
     * 执行服务调用环节（修正版：使用环节出参配置）
     */
    private Map<String, Object> executeServiceNode(Node node, Map<String, Map<String, Object>> context) {
        String nodeId = node.getNodeId();
        ServiceDef service = serviceDefService.findById(node.getServiceId());
        List<NodeInputMapping> mappings = mappingRepository.findByNodeId(nodeId);

        // 组装服务入参
        Map<String, Object> serviceInput = new HashMap<>();
        for (NodeInputMapping mapping : mappings) {
            ServiceInputParam inputParam = serviceInputParamRepository.findById(mapping.getServiceInputParamId())
                    .orElseThrow(() -> new RuntimeException("服务入参配置不存在, id=" + mapping.getServiceInputParamId()));
            String paramName = inputParam.getParamName();
            Object value = resolveMappingValue(mapping, context, nodeId);
            if (value != null) {
                serviceInput.put(paramName, value);
            } else if (inputParam.getRequired()) {
                throw new RuntimeException("服务入参必需但未提供或解析失败: " + paramName);
            }
        }

        logger.info("服务调用入参: {}", serviceInput);
        Map<String, Object> response = serviceInvoker.invoke(service, serviceInput);

        // 关键修改：使用环节出参配置（NodeOutputParam）进行输出提取和嵌套构建
        List<NodeOutputParam> outputParams = paramConfigService.findOutputParamsByNodeId(nodeId);
        Map<String, Object> extractedOutput = extractOutputs(outputParams, response);

        // 存入上下文
        context.put(nodeId + "_out", extractedOutput);
        if (!context.containsKey(nodeId)) {
            context.put(nodeId, new HashMap<>());
        }

        return extractedOutput;
    }

    /**
     * 根据映射配置解析值
     */
    private Object resolveMappingValue(NodeInputMapping mapping, Map<String, Map<String, Object>> context,
                                       String currentNodeId) {
        String sourceType = mapping.getSourceType();
        String sourceValue = mapping.getSourceValue();

        if (sourceValue == null) {
            return null;
        }

        switch (sourceType) {
            case "constant":
                return sourceValue;
            case "current_input":
                Map<String, Object> currentInput = context.getOrDefault(currentNodeId, new HashMap<>());
                return currentInput.get(sourceValue);
            case "context_field":
                try {
                    return expressionEvaluator.evaluate(sourceValue, context);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("上下文表达式解析失败: " + e.getMessage(), e);
                }
            default:
                throw new RuntimeException("未知的 sourceType: " + sourceType);
        }
    }

    /**
     * 解析入参（原有逻辑）
     */
    private Map<String, Object> resolveInputs(List<NodeInputParam> inputParams,
                                              Map<String, Map<String, Object>> context,
                                              String currentNodeId) {
        Map<String, Object> inputs = new HashMap<>();
        for (NodeInputParam param : inputParams) {
            String paramName = param.getParamName();
            String source = param.getParamSource();
            String expression = param.getSourceExpression();
            Object value = null;

            switch (source) {
                case "request":
                    Map<String, Object> currentInput = context.getOrDefault(currentNodeId, new HashMap<>());
                    value = currentInput.get(paramName);
                    break;
                case "context":
                    if (expression != null && !expression.isEmpty()) {
                        value = expressionEvaluator.evaluate(expression, context);
                    }
                    break;
                case "constant":
                    value = expression;
                    break;
                default:
                    logger.warn("未知的参数来源: {}", source);
            }

            if (value == null && param.getRequired()) {
                throw new IllegalArgumentException("必需参数缺失: " + paramName);
            }
            if (value != null) {
                inputs.put(paramName, value);
            }
        }
        return inputs;
    }

    /**
     * 提取输出字段（支持 JSONPath/SpEL 表达式和嵌套参数名）
     */
    private Map<String, Object> extractOutputs(List<NodeOutputParam> outputParams, Map<String, Object> rawOutput) {
        if (outputParams.isEmpty()) {
            return rawOutput == null ? new HashMap<>() : rawOutput;
        }
        Map<String, Object> result = new HashMap<>();
        for (NodeOutputParam param : outputParams) {
            String name = param.getParamName();
            String expr = param.getParamValueExpression();
            Object value;
            if (expr != null && !expr.isEmpty()) {
                value = expressionEvaluator.evaluateOnObject(expr, rawOutput);
            } else {
                value = rawOutput == null ? null : rawOutput.get(name);
            }
            if (value == null) {
                continue;
            }
            // 处理嵌套路径（如 "ROOT.BODY.RETURN_CODE"）
            String[] parts = name.split("\\.");
            Map<String, Object> current = result;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                if (!current.containsKey(part)) {
                    current.put(part, new HashMap<String, Object>());
                }
                Object next = current.get(part);
                if (!(next instanceof Map)) {
                    next = new HashMap<String, Object>();
                    current.put(part, next);
                }
                current = (Map<String, Object>) next;
            }
            current.put(parts[parts.length - 1], value);
        }
        return result;
    }
}
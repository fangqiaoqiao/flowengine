package com.fqq.flowengine.util;

import com.fqq.flowengine.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 上下文辅助类
 * 提供根据环节ID获取指定字段的便捷方法
 */
@Component
public class ContextHelper {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ExpressionEvaluator expressionEvaluator;

    /**
     * 从Redis上下文中获取指定环节的某个字段值
     *
     * @param componentId 组件ID
     * @param keyId       流程唯一标识
     * @param nodeId      环节ID（可以是原始环节ID或添加了"_out"后缀的）
     * @param fieldName   字段名
     * @return 字段值，如果不存在则返回null
     */
    public Object getFieldFromNode(String componentId, String keyId, String nodeId, String fieldName) {
        Map<String, Map<String, Object>> context = redisService.getContext(componentId, keyId);
        if (context == null) {
            return null;
        }
        Map<String, Object> nodeData = context.get(nodeId);
        if (nodeData == null) {
            // 尝试加 "_out" 后缀，因为输出通常存储为 nodeId_out
            nodeData = context.get(nodeId + "_out");
        }
        if (nodeData != null && fieldName != null) {
            return nodeData.get(fieldName);
        }
        return null;
    }

    /**
     * 获取整个环节的输出（Map）
     *
     * @param componentId 组件ID
     * @param keyId       流程唯一标识
     * @param nodeId      环节ID
     * @return 该环节的输出（Map），若不存在则返回null
     */
    public Map<String, Object> getNodeOutput(String componentId, String keyId, String nodeId) {
        Map<String, Map<String, Object>> context = redisService.getContext(componentId, keyId);
        if (context == null) {
            return null;
        }
        // 优先使用 nodeId_out
        Map<String, Object> output = context.get(nodeId + "_out");
        if (output == null) {
            // 兼容直接存储于 nodeId 的情况
            output = context.get(nodeId);
        }
        return output;
    }

    /**
     * 使用表达式语法（如 ${SQ01.userId}）从上下文中取值
     *
     * @param componentId 组件ID
     * @param keyId       流程唯一标识
     * @param expression  表达式，如 "${SQ01.userId}"
     * @return 表达式求值结果
     */
    public Object evaluateExpression(String componentId, String keyId, String expression) {
        Map<String, Map<String, Object>> context = redisService.getContext(componentId, keyId);
        if (context == null) {
            return null;
        }
        return expressionEvaluator.evaluate(expression, context);
    }
}
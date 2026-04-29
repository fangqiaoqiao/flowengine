package com.fqq.flowengine.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式求值器 - 改进版
 * 支持：
 * 1. 上下文占位符替换：${nodeId.field}，可出现在字符串任意位置，支持多个占位符混合。
 * 2. 针对单个对象的 JSONPath ($.path) 和 SpEL 求值（用于出参映射）。
 */
@Component
public class ExpressionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionEvaluator.class);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private final ExpressionParser spelParser = new SpelExpressionParser();

    /**
     * 解析字符串中的占位符（支持多个混合），从上下文中取值替换。
     * 如果任何占位符无法解析，抛出 IllegalArgumentException。
     *
     * @param expression 可能包含占位符的字符串，例如 "hello ${SQ01.name}, age ${SQ01.age}"
     * @param context    流程上下文 Map<节点ID, Map<字段名, 值>>
     * @return 替换后的字符串，如果表达式为 null 或空则返回原值
     * @throws IllegalArgumentException 当占位符无法找到对应值时抛出
     */
    public String evaluate(String expression, Map<String, Map<String, Object>> context) {
        if (expression == null || expression.trim().isEmpty()) {
            return expression;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(expression);
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String path = matcher.group(1); // 例如 "SQ01.loginNo"
            Object value = resolveContextPath(path, context);
            if (value == null) {
                String errorMsg = String.format("无法解析占位符: ${%s}，表达式: %s，请确保上下文包含节点 '%s' 且字段 '%s' 存在",
                        path, expression, getNodeIdFromPath(path), getFieldFromPath(path));
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            // 将值转为字符串（如果是基本类型或已有 toString 的对象）
            String replacement = value.toString();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        if (found) {
            matcher.appendTail(result);
            return result.toString();
        } else {
            // 没有占位符，直接返回原字符串（可能是纯常量）
            return expression;
        }
    }

    /**
     * 解析路径 "nodeId.field" 从上下文中取具体的字段值
     *
     * @param path    路径，如 "SQ01.loginNo"
     * @param context 上下文
     * @return 字段值，未找到返回 null
     */
    private Object resolveContextPath(String path, Map<String, Map<String, Object>> context) {
        if (path == null || context == null) return null;

        int dotIndex = path.indexOf('.');
        if (dotIndex == -1) {
            // 没有点，认为取整个节点的数据（较少用）
            return context.get(path);
        }

        String nodeId = path.substring(0, dotIndex);
        String field = path.substring(dotIndex + 1);

        Map<String, Object> nodeData = context.get(nodeId);
        if (nodeData == null) {
            logger.debug("上下文不存在节点: {}", nodeId);
            return null;
        }
        return nodeData.get(field);
    }

    private String getNodeIdFromPath(String path) {
        int dotIndex = path.indexOf('.');
        return dotIndex == -1 ? path : path.substring(0, dotIndex);
    }

    private String getFieldFromPath(String path) {
        int dotIndex = path.indexOf('.');
        return dotIndex == -1 ? "" : path.substring(dotIndex + 1);
    }

    /**
     * 对单个对象执行表达式求值（用于出参映射）
     * 支持 JSONPath (以 $. 开头) 和 SpEL
     *
     * @param expression 表达式
     * @param root       根对象（通常是处理器返回的 Map）
     * @return 求值结果
     */
    public Object evaluateOnObject(String expression, Object root) {
        if (expression == null || expression.trim().isEmpty() || root == null) {
            return null;
        }

        // JSONPath (以 "$." 开头)
        if (expression.startsWith("$.")) {
            try {
                return JsonPath.read(root, expression);
            } catch (Exception e) {
                logger.warn("JSONPath evaluation failed: {}", expression, e);
                return null;
            }
        }

        // SpEL
        try {
            StandardEvaluationContext evalContext = new StandardEvaluationContext(root);
            Expression exp = spelParser.parseExpression(expression);
            return exp.getValue(evalContext);
        } catch (Exception e) {
            logger.warn("SpEL evaluation failed: {}", expression, e);
            return null;
        }
    }
}
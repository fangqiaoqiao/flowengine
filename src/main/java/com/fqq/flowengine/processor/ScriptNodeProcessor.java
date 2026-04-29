package com.fqq.flowengine.processor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Groovy脚本处理器
 * processorConfig 为 Groovy 脚本字符串
 * 脚本中应定义一个 `run` 方法接收 `input` Map 并返回结果 Map，
 * 或者直接编写顶层代码，最后将结果赋值给 `result` 变量。
 * 推荐写法：
 *   def result = [:]
 *   result.data = input.someField
 *   return result
 */
public class ScriptNodeProcessor implements NodeProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ScriptNodeProcessor.class);

    @Override
    public Map<String, Object> process(Map<String, Object> input, String processorConfig) {
        if (processorConfig == null || processorConfig.trim().isEmpty()) {
            throw new IllegalArgumentException("Script processor requires non-empty script content");
        }

        Binding binding = new Binding();
        binding.setVariable("input", input);
        // 附加一些辅助工具可以按需添加

        GroovyShell shell = new GroovyShell(binding);
        try {
            Script script = shell.parse(processorConfig);
            Object result = script.run();
            if (result instanceof Map) {
                return (Map<String, Object>) result;
            } else {
                // 如果脚本返回非Map，包装一下
                Map<String, Object> wrapped = new HashMap<>();
                wrapped.put("result", result);
                return wrapped;
            }
        } catch (Exception e) {
            logger.error("Script execution error", e);
            throw new RuntimeException("Script execution failed: " + e.getMessage(), e);
        }
    }
}
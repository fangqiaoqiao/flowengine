package com.fqq.flowengine.processor;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器工厂
 * 根据 processorType 返回对应的处理器实例
 */
@Component
public class NodeProcessorFactory {

    private final Map<String, NodeProcessor> processors = new ConcurrentHashMap<>();

    public NodeProcessorFactory() {
        // 注册内置处理器
        processors.put("http", new HttpNodeProcessor());
        processors.put("script", new ScriptNodeProcessor());
        processors.put("dummy", new DummyNodeProcessor());
    }

    /**
     * 获取处理器实例
     * @param type 类型标识（http, script, dummy）
     * @return 对应的处理器
     * @throws IllegalArgumentException 如果类型不支持
     */
    public NodeProcessor getProcessor(String type) {
        NodeProcessor processor = processors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported processor type: " + type);
        }
        return processor;
    }

    /**
     * 动态注册新的处理器（用于扩展）
     * @param type 类型标识
     * @param processor 处理器实例
     */
    public void registerProcessor(String type, NodeProcessor processor) {
        processors.put(type, processor);
    }
}
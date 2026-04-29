package com.fqq.flowengine.processor;

import java.util.Map;

/**
 * 节点处理器接口
 * 所有环节的执行逻辑都需实现此接口
 */
public interface NodeProcessor {
    /**
     * 执行处理逻辑
     * @param input 解析后的入参（已根据配置映射好）
     * @param processorConfig 处理器配置字符串（JSON格式或脚本内容）
     * @return 处理结果（Map类型，后续可进行出参映射）
     */
    Map<String, Object> process(Map<String, Object> input, String processorConfig);
}
package com.fqq.flowengine.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 空处理器（仅回传入参，可用于测试）
 */
public class DummyNodeProcessor implements NodeProcessor {

    @Override
    public Map<String, Object> process(Map<String, Object> input, String processorConfig) {
        // 不做任何处理，原样返回入参，也可加上 config 中的额外信息
        Map<String, Object> result = new HashMap<>(input);
        result.put("_processor", "dummy");
        return result;
    }
}
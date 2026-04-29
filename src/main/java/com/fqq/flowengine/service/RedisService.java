package com.fqq.flowengine.service;

import java.util.Map;

public interface RedisService {
    void saveContext(String componentId, String keyId, Map<String, Map<String, Object>> context);
    Map<String, Map<String, Object>> getContext(String componentId, String keyId);
    void expireContext(String componentId, String keyId, int seconds);
    void deleteContext(String componentId, String keyId);

    void setLatestKeyId(String componentId, String keyId);
    String getLatestKeyId(String componentId);
    void expireLatestKeyId(String componentId, int seconds);
    void deleteLatestKeyId(String componentId);

    // 更新某环节的输出（局部更新上下文）
    void updateNodeOutput(String componentId, String keyId, String nodeId, Map<String, Object> output);
}
package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.service.RedisService;
import com.fqq.flowengine.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private static final String CONTEXT_PREFIX = "flow:ctx:";
    private static final String LATEST_KEY_PREFIX = "flow:latestKey:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveContext(String componentId, String keyId, Map<String, Map<String, Object>> context) {
        String key = CONTEXT_PREFIX + componentId + ":" + keyId;
        // 使用 Hash 存储整个 context 作为一个 field 的 JSON 字符串，
        // 或者存储为多个 field。这里为了简单，序列化整个 context 为一个 JSON
        redisTemplate.opsForValue().set(key, context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getContext(String componentId, String keyId) {
        String key = CONTEXT_PREFIX + componentId + ":" + keyId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Map) {
            return (Map<String, Map<String, Object>>) value;
        }
        return null;
    }

    @Override
    public void expireContext(String componentId, String keyId, int seconds) {
        String key = CONTEXT_PREFIX + componentId + ":" + keyId;
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void deleteContext(String componentId, String keyId) {
        String key = CONTEXT_PREFIX + componentId + ":" + keyId;
        redisTemplate.delete(key);
    }

    @Override
    public void setLatestKeyId(String componentId, String keyId) {
        String key = LATEST_KEY_PREFIX + componentId;
        stringRedisTemplate.opsForValue().set(key, keyId);
    }

    @Override
    public String getLatestKeyId(String componentId) {
        String key = LATEST_KEY_PREFIX + componentId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void expireLatestKeyId(String componentId, int seconds) {
        String key = LATEST_KEY_PREFIX + componentId;
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void deleteLatestKeyId(String componentId) {
        String key = LATEST_KEY_PREFIX + componentId;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void updateNodeOutput(String componentId, String keyId, String nodeId, Map<String, Object> output) {
        Map<String, Map<String, Object>> context = getContext(componentId, keyId);
        if (context != null) {
            context.put(nodeId + "_out", output);
            saveContext(componentId, keyId, context);
            expireContext(componentId, keyId, 1800); // 重设过期时间
        }
    }
}
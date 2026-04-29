package com.fqq.flowengine.controller;

import com.fqq.flowengine.exception.KeyIdInvalidException;
import com.fqq.flowengine.service.FlowEngineService;
import com.fqq.flowengine.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 环节执行控制器
 * 处理：/flowengine/service/{componentId}/{nodeId}?keyid=xxx
 *
 * @author fqq
 */
@RestController
@RequestMapping("/flowengine/service")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private FlowEngineService flowEngineService;

    @Autowired
    private RedisService redisService;

    /**
     * 执行指定环节
     *
     * @param componentId 组件ID
     * @param nodeId      环节ID
     * @param keyId       查询参数keyid
     * @param requestBody 当前环节的额外入参（可选，会与配置的入参合并）
     * @return 环节执行结果
     */
    // ServiceController.java 中的 executeNode 方法

    @PostMapping("/{componentId}/{nodeId}")
    public ResponseEntity<?> executeNode(
            @PathVariable String componentId,
            @PathVariable String nodeId,
            @RequestParam("keyid") String keyId,
            @RequestBody(required = false) Map<String, Object> requestBody) {

        logger.info("执行环节，componentId={}, nodeId={}, keyId={}", componentId, nodeId, keyId);

        // 1. 校验 keyId 是否为该组件最新的
        String latestKeyId = redisService.getLatestKeyId(componentId);
        if (latestKeyId == null || !latestKeyId.equals(keyId)) {
            logger.warn("keyId无效或不是最新，componentId={}, 传入keyId={}, 最新keyId={}", componentId, keyId, latestKeyId);
            throw new KeyIdInvalidException("keyId无效、已过期或不是最新值，请重新调用场景入口");
        }

        // 2. 检查 keyId 对应的上下文是否存在
        Map<String, Map<String, Object>> context = redisService.getContext(componentId, keyId);
        if (context == null) {
            throw new KeyIdInvalidException("上下文已过期，请重新调用场景入口");
        }

        // 3. 合并请求体到当前环节的入参
        Map<String, Object> currentInput = context.getOrDefault(nodeId, new HashMap<>());
        if (requestBody != null && !requestBody.isEmpty()) {
            currentInput.putAll(requestBody);
        }
        context.put(nodeId, currentInput);

        // 4. 执行环节
        Map<String, Object> output;
        try {
            output = flowEngineService.executeNode(componentId, nodeId, context);
        } catch (Exception e) {
            logger.error("环节执行失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("message", "环节执行失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }

        // 5. 将输出存入上下文
        // 注意：executeNode 内部已调用 redisService.updateNodeOutput

        // 6. 刷新上下文的过期时间
        redisService.expireContext(componentId, keyId, 1800);
        redisService.expireLatestKeyId(componentId, 1800);

        // 7. 直接返回环节输出的 Map（即用户自定义的出参字段）
        return ResponseEntity.ok(output);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test(@RequestBody(required = false) Map<String, Object> requestBody) {

        logger.info("服务入参={}", requestBody);
        // 7. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        return ResponseEntity.ok(result);
    }
}
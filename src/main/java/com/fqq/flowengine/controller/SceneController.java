package com.fqq.flowengine.controller;

import com.fqq.flowengine.model.dto.SceneRequest;
import com.fqq.flowengine.model.dto.SceneResponse;
import com.fqq.flowengine.service.FlowEngineService;
import com.fqq.flowengine.service.RedisService;
import com.fqq.flowengine.util.KeyIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 场景入口控制器
 * 处理流程启动请求：/flowengine/scene/{componentId}
 *
 * @author fqq
 */
@RestController
@RequestMapping("/flowengine/scene")
public class SceneController {

    private static final Logger logger = LoggerFactory.getLogger(SceneController.class);

    @Autowired
    private FlowEngineService flowEngineService;

    @Autowired
    private RedisService redisService;

    /**
     * 启动一个组件流程
     *
     * @param componentId 组件ID，如 SQ007070
     * @param requestBody 请求体，作为组件第一个环节的入参
     * @return 包含keyId的响应
     */
    @PostMapping("/{componentId}")
    public ResponseEntity<Map<String, Object>> startScene(
            @PathVariable String componentId,
            @RequestBody(required = false) Map<String, Object> requestBody) {

        logger.info("启动场景，componentId={}, requestBody={}", componentId, requestBody);

        // 1. 生成唯一keyId
        String keyId = KeyIdGenerator.generate();

        // 2. 获取组件的第一个环节（环节顺序最小的）
        String firstNodeId = flowEngineService.getFirstNodeId(componentId);
        if (firstNodeId == null) {
            throw new RuntimeException("组件 " + componentId + " 未配置任何环节");
        }

        // 3. 构建初始上下文：将请求体作为第一个环节的入参
        Map<String, Map<String, Object>> context = new HashMap<>();
        context.put(firstNodeId, requestBody == null ? new HashMap<>() : requestBody);

        // 4. 保存上下文到Redis，设置30分钟过期
        redisService.saveContext(componentId, keyId, context);
        redisService.expireContext(componentId, keyId, 1800); // 30分钟

        // 5. 更新组件的最新keyId（覆盖旧的，也设置30分钟过期）
        redisService.setLatestKeyId(componentId, keyId);
        redisService.expireLatestKeyId(componentId, 1800);

        logger.info("场景启动成功，componentId={}, keyId={}", componentId, keyId);

        // 6. 返回keyId
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        SceneResponse data = new SceneResponse();
        data.setKeyId(keyId);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
}
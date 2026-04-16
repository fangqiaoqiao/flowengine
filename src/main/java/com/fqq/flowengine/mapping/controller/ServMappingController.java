package com.fqq.flowengine.mapping.controller;

import com.fqq.flowengine.mapping.service.ServMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/flowengine/service")
@Slf4j
public class ServMappingController {

    @Resource
    private ServMappingService servMappingService;

    /**
     * 处理映射请求入口
     *
     * @param firstCode  第一个编码参数，从路径中获取
     * @param secondCode 第二个编码参数，从路径中获取
     * @param inMap      请求体中的输入参数映射
     * @return 响应实体，包含处理后的输出参数和HTTP状态码
     */
    @RequestMapping("/{firstCode}/{secondCode}")
    public ResponseEntity<?> handleRequest(
            @PathVariable String firstCode,
            @PathVariable String secondCode,
            @RequestBody Map<String, Object> inMap) {
        // 记录请求日志
        log.info("/{}/{};in={}", firstCode, secondCode, inMap);
        return servMappingService.mapToOutParam(firstCode, secondCode, inMap);
    }


}

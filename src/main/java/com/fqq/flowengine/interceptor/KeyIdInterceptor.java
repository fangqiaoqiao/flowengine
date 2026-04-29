package com.fqq.flowengine.interceptor;

import com.fqq.flowengine.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 可选拦截器：用于全局校验 keyId
 * 注意：当前 ServiceController 内部已包含 keyId 校验，因此本拦截器并非必须。
 * 若需要统一校验所有 /flowengine/service/** 请求，可启用。
 */
@Component
public class KeyIdInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 仅对 POST 请求生效
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        // 期望格式：/flowengine/service/{componentId}/{nodeId}
        String[] parts = uri.split("/");
        if (parts.length < 5) {
            // 格式不匹配，放行（后续 Controller 会处理）
            return true;
        }
        String componentId = parts[3];
        String keyId = request.getParameter("keyid");
        if (keyId == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"code\":400,\"message\":\"缺少 keyid 参数\",\"data\":null}");
            return false;
        }
        String latestKeyId = redisService.getLatestKeyId(componentId);
        if (latestKeyId == null || !latestKeyId.equals(keyId)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"code\":400,\"message\":\"keyId 无效或不是最新值\",\"data\":null}");
            return false;
        }
        return true;
    }
}
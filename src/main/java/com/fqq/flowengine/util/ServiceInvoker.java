package com.fqq.flowengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqq.flowengine.model.entity.ServiceDef;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> invoke(ServiceDef service, Map<String, Object> requestParams) {
        if (service == null) {
            throw new IllegalArgumentException("Service definition cannot be null");
        }
        String url = service.getUrl();
        String method = service.getMethod() != null ? service.getMethod().toUpperCase() : "POST";
        if (!"POST".equals(method)) {
            throw new UnsupportedOperationException("Only POST method is supported currently");
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            // 设置请求头
            String headersJson = service.getHeaders();
            if (headersJson != null && !headersJson.isEmpty()) {
                Map<String, String> headers = objectMapper.readValue(headersJson, Map.class);
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置请求体
            String body = objectMapper.writeValueAsString(requestParams);
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            long start = System.currentTimeMillis();
            try (CloseableHttpResponse response = client.execute(post)) {
                long cost = System.currentTimeMillis() - start;
                HttpEntity entity = response.getEntity();
                String respBody = entity != null ? EntityUtils.toString(entity) : "";
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    return objectMapper.readValue(respBody, Map.class);
                } else {
                    logger.error("Service invoke failed, status={}, body={}", status, respBody);
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "HTTP " + status);
                    error.put("message", respBody);
                    return error;
                }
            }
        } catch (Exception e) {
            logger.error("Service invoke error", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invocation failed");
            error.put("message", e.getMessage());
            return error;
        }
    }
}
package com.fqq.flowengine.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP转发处理器
 * processorConfig 格式示例：
 * {
 *   "url": "http://example.com/api",
 *   "method": "POST",
 *   "headers": {"Content-Type": "application/json"}
 * }
 */
public class HttpNodeProcessor implements NodeProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpNodeProcessor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> process(Map<String, Object> input, String processorConfig) {
        Map<String, Object> config = parseConfig(processorConfig);
        if (config == null || !config.containsKey("url")) {
            throw new IllegalArgumentException("HTTP processor requires 'url' in config");
        }

        String url = (String) config.get("url");
        String method = config.getOrDefault("method", "POST").toString().toUpperCase();

        // 构建请求体（默认为input的JSON）
        String body;
        try {
            body = objectMapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize input to JSON", e);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpUriRequest request = buildRequest(url, method, body, config);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseBody = entity != null ? EntityUtils.toString(entity) : "";
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    // 尝试解析为JSON Map，若失败则返回原始字符串
                    try {
                        return objectMapper.readValue(responseBody, Map.class);
                    } catch (Exception e) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("rawResponse", responseBody);
                        return result;
                    }
                } else {
                    throw new RuntimeException("HTTP request failed, status=" + statusCode + ", body=" + responseBody);
                }
            }
        } catch (IOException e) {
            logger.error("HTTP request error", e);
            throw new RuntimeException("HTTP request error: " + e.getMessage(), e);
        }
    }

    private HttpUriRequest buildRequest(String url, String method, String body, Map<String, Object> config) {
        if ("POST".equalsIgnoreCase(method)) {
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            applyHeaders(post, config);
            return post;
        } else {
            throw new UnsupportedOperationException("HTTP method " + method + " not implemented yet");
        }
    }

    private void applyHeaders(HttpUriRequest request, Map<String, Object> config) {
        Object headersObj = config.get("headers");
        if (headersObj instanceof Map) {
            Map<String, String> headers = (Map<String, String>) headersObj;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(configJson, Map.class);
        } catch (Exception e) {
            logger.warn("Failed to parse processor config as JSON, treat as empty", e);
            return new HashMap<>();
        }
    }
}
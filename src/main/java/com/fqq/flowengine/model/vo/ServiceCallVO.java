package com.fqq.flowengine.model.vo;

import java.util.Map;

/**
 * 服务调用视图对象（用于监控或调试）
 */
public class ServiceCallVO {

    private String serviceCode;
    private String serviceName;
    private String url;
    private String method;
    private Map<String, Object> requestParams;
    private Map<String, Object> responseBody;
    private Map<String, Object> extractedOutput;
    private long costTimeMillis;
    private boolean success;
    private String errorMessage;

    public ServiceCallVO() {
    }

    // Getters and Setters
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public Map<String, Object> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Map<String, Object> responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, Object> getExtractedOutput() {
        return extractedOutput;
    }

    public void setExtractedOutput(Map<String, Object> extractedOutput) {
        this.extractedOutput = extractedOutput;
    }

    public long getCostTimeMillis() {
        return costTimeMillis;
    }

    public void setCostTimeMillis(long costTimeMillis) {
        this.costTimeMillis = costTimeMillis;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
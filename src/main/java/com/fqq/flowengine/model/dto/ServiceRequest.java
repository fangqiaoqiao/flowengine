package com.fqq.flowengine.model.dto;

import java.util.Map;

/**
 * 环节执行请求体（当前环节的附加入参）
 */
public class ServiceRequest {

    private Map<String, Object> params;

    public ServiceRequest() {
    }

    public ServiceRequest(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
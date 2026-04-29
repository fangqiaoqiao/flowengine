package com.fqq.flowengine.model.vo;

import java.util.Map;

/**
 * 运行时上下文视图对象（用于前端展示上下文内容）
 */
public class ContextVO {

    private String componentId;
    private String keyId;
    private Map<String, Map<String, Object>> context; // nodeId -> 入参/输出等

    public ContextVO() {
    }

    public ContextVO(String componentId, String keyId, Map<String, Map<String, Object>> context) {
        this.componentId = componentId;
        this.keyId = keyId;
        this.context = context;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Map<String, Map<String, Object>> getContext() {
        return context;
    }

    public void setContext(Map<String, Map<String, Object>> context) {
        this.context = context;
    }
}
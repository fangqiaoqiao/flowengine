package com.fqq.flowengine.model.dto;

/**
 * 场景入口响应，仅包含keyId
 */
public class SceneResponse {

    private String keyId;

    public SceneResponse() {
    }

    public SceneResponse(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
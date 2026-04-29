package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 场景入口请求体（实际上可以是任意JSON，此处定义为一个通用Map包装）
 */
public class SceneRequest {

    @NotNull(message = "input cannot be null")
    private Map<String, Object> input;

    public SceneRequest() {
    }

    public SceneRequest(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }
}
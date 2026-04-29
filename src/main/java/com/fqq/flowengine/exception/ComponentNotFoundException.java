package com.fqq.flowengine.exception;

/**
 * 组件不存在异常
 */
public class ComponentNotFoundException extends BusinessException {

    public ComponentNotFoundException(String message) {
        super(404, message);
    }

    public ComponentNotFoundException(String componentId, Throwable cause) {
        super(404, "组件不存在: " + componentId);
        initCause(cause);
    }
}
package com.fqq.flowengine.exception;

/**
 * keyId无效或过期异常
 */
public class KeyIdInvalidException extends BusinessException {

    public KeyIdInvalidException(String message) {
        super(400, message);
    }

    public KeyIdInvalidException(String componentId, String keyId) {
        super(400, "keyId无效或已过期: componentId=" + componentId + ", keyId=" + keyId);
    }

    public KeyIdInvalidException(String message, Throwable cause) {
        super(400, message);
        initCause(cause);
    }
}
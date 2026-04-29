package com.fqq.flowengine.exception;

/**
 * 环节不存在异常
 */
public class NodeNotFoundException extends BusinessException {

    public NodeNotFoundException(String message) {
        super(404, message);
    }

    public NodeNotFoundException(String nodeId, Throwable cause) {
        super(404, "环节不存在: " + nodeId);
        initCause(cause);
    }
}
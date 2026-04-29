package com.fqq.flowengine.service;

import java.util.Map;

public interface FlowEngineService {
    String getFirstNodeId(String componentId);
    Map<String, Object> executeNode(String componentId, String nodeId, Map<String, Map<String, Object>> context);
}
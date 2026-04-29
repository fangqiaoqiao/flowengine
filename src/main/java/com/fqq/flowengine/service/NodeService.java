package com.fqq.flowengine.service;

import com.fqq.flowengine.model.entity.Node;

import java.util.List;

public interface NodeService {
    List<Node> findByComponentIdOrderByNodeOrder(String componentId);
    Node findByNodeId(String nodeId);
    Node save(Node node);
    void deleteByNodeId(String nodeId);
    String getFirstNodeId(String componentId);
}
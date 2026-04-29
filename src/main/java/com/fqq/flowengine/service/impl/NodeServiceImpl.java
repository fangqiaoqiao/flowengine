package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.model.entity.Node;
import com.fqq.flowengine.exception.NodeNotFoundException;
import com.fqq.flowengine.repository.NodeRepository;
import com.fqq.flowengine.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public List<Node> findByComponentIdOrderByNodeOrder(String componentId) {
        return nodeRepository.findByComponentIdOrderByNodeOrderAsc(componentId);
    }

    @Override
    public Node findByNodeId(String nodeId) {
        return nodeRepository.findByNodeId(nodeId)
                .orElseThrow(() -> new NodeNotFoundException("环节不存在: " + nodeId));
    }

    @Override
    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public void deleteByNodeId(String nodeId) {
        Node node = findByNodeId(nodeId);
        nodeRepository.delete(node);
    }

    @Override
    public String getFirstNodeId(String componentId) {
        // 使用 ROWNUM 方式，兼容 Oracle 11g+
        return nodeRepository.findFirstNodeIdByComponentId(componentId).orElse(null);
    }
}
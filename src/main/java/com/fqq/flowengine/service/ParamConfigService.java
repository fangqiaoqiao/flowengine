package com.fqq.flowengine.service;

import com.fqq.flowengine.model.entity.NodeInputParam;
import com.fqq.flowengine.model.entity.NodeOutputParam;

import java.util.List;

public interface ParamConfigService {
    // Input
    List<NodeInputParam> findInputParamsByNodeId(String nodeId);
    NodeInputParam findInputParamById(Long id);
    NodeInputParam saveInputParam(NodeInputParam param);
    void deleteInputParam(Long id);

    // Output
    List<NodeOutputParam> findOutputParamsByNodeId(String nodeId);
    NodeOutputParam findOutputParamById(Long id);
    NodeOutputParam saveOutputParam(NodeOutputParam param);
    void deleteOutputParam(Long id);
}
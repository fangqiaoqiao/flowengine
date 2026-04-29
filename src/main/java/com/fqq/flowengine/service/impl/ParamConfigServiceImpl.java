package com.fqq.flowengine.service.impl;

import com.fqq.flowengine.model.entity.NodeInputParam;
import com.fqq.flowengine.model.entity.NodeOutputParam;
import com.fqq.flowengine.exception.BusinessException;
import com.fqq.flowengine.repository.NodeInputParamRepository;
import com.fqq.flowengine.repository.NodeOutputParamRepository;
import com.fqq.flowengine.service.ParamConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParamConfigServiceImpl implements ParamConfigService {

    @Autowired
    private NodeInputParamRepository inputParamRepository;

    @Autowired
    private NodeOutputParamRepository outputParamRepository;

    // ---------- Input ----------
    @Override
    public List<NodeInputParam> findInputParamsByNodeId(String nodeId) {
        return inputParamRepository.findByNodeIdOrderByIdAsc(nodeId);
    }

    @Override
    public NodeInputParam findInputParamById(Long id) {
        return inputParamRepository.findById(id)
                .orElseThrow(() -> new BusinessException("入参配置不存在: id=" + id));
    }

    @Override
    public NodeInputParam saveInputParam(NodeInputParam param) {
        return inputParamRepository.save(param);
    }

    @Override
    public void deleteInputParam(Long id) {
        inputParamRepository.deleteById(id);
    }

    // ---------- Output ----------
    @Override
    public List<NodeOutputParam> findOutputParamsByNodeId(String nodeId) {
        return outputParamRepository.findByNodeIdOrderByIdAsc(nodeId);
    }

    @Override
    public NodeOutputParam findOutputParamById(Long id) {
        return outputParamRepository.findById(id)
                .orElseThrow(() -> new BusinessException("出参配置不存在: id=" + id));
    }

    @Override
    public NodeOutputParam saveOutputParam(NodeOutputParam param) {
        return outputParamRepository.save(param);
    }

    @Override
    public void deleteOutputParam(Long id) {
        outputParamRepository.deleteById(id);
    }
}
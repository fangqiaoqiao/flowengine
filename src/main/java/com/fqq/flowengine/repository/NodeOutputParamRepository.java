package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.NodeOutputParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeOutputParamRepository extends JpaRepository<NodeOutputParam, Long> {
    List<NodeOutputParam> findByNodeIdOrderByIdAsc(String nodeId);
    void deleteByNodeId(String nodeId);
}
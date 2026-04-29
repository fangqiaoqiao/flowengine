package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.NodeInputParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeInputParamRepository extends JpaRepository<NodeInputParam, Long> {
    List<NodeInputParam> findByNodeIdOrderByIdAsc(String nodeId);
    void deleteByNodeId(String nodeId);
}
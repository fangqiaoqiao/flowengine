package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.NodeInputMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeInputMappingRepository extends JpaRepository<NodeInputMapping, Long> {
    List<NodeInputMapping> findByNodeId(String nodeId);
    void deleteByNodeId(String nodeId);
    void deleteByServiceInputParamId(Long serviceInputParamId);
}
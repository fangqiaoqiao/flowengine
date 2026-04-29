package com.fqq.flowengine.repository;

import com.fqq.flowengine.model.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    List<Node> findByComponentIdOrderByNodeOrderAsc(String componentId);

    Optional<Node> findByNodeId(String nodeId);

    void deleteByNodeId(String nodeId);

    /**
     * 查询组件下的第一个环节ID（按 nodeOrder 升序取第一条，兼容 Oracle 11g）
     * @param componentId 组件ID
     * @return 最多一条记录的列表
     */
    @Query(value = "SELECT node_id FROM node WHERE component_id = ?1 ORDER BY node_order ASC FETCH FIRST 1 ROW ONLY", nativeQuery = true)
    List<String> findFirstNodeIdByComponentIdWithFetchFirst(String componentId);

    // 更通用的方式（使用 ROWNUM，兼容所有 Oracle 版本）
    @Query(value = "SELECT node_id FROM (SELECT node_id FROM node WHERE component_id = ?1 ORDER BY node_order ASC) WHERE ROWNUM = 1", nativeQuery = true)
    Optional<String> findFirstNodeIdByComponentId(@Param("componentId") String componentId);
}
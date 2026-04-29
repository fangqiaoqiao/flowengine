package com.fqq.flowengine.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 环节实体类
 * 对应表：node
 */
@Entity
@Table(name = "node")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_node")
    @SequenceGenerator(name = "seq_node", sequenceName = "seq_node_id", allocationSize = 1)
    private Long id;

    @Column(name = "node_id", nullable = false, unique = true, length = 50)
    private String nodeId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "component_id", nullable = false, length = 50)
    private String componentId;

    @Column(name = "node_name", nullable = false, length = 100)
    private String nodeName;

    @Column(name = "node_order", nullable = false)
    private Integer nodeOrder;

    @Column(name = "processor_type", nullable = false, length = 50)
    private String processorType;  // http, script, dummy

    @Column(name = "processor_config", columnDefinition = "CLOB")
    private String processorConfig;  // JSON配置，如HTTP URL、脚本内容等

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public String getProcessorConfig() {
        return processorConfig;
    }

    public void setProcessorConfig(String processorConfig) {
        this.processorConfig = processorConfig;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
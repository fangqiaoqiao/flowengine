package com.fqq.flowengine.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "node_input_mapping")
public class NodeInputMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nim")
    @SequenceGenerator(name = "seq_nim", sequenceName = "seq_nim_id", allocationSize = 1)
    private Long id;

    @Column(name = "node_id", nullable = false, length = 50)
    private String nodeId;

    @Column(name = "service_input_param_id", nullable = false)
    private Long serviceInputParamId;

    @Column(name = "source_type", nullable = false, length = 30)
    private String sourceType;   // constant, current_input, context_field

    @Column(name = "source_value", length = 500)
    private String sourceValue;   // 常量值、字段名或上下文表达式

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Long getServiceInputParamId() { return serviceInputParamId; }
    public void setServiceInputParamId(Long serviceInputParamId) { this.serviceInputParamId = serviceInputParamId; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceValue() { return sourceValue; }
    public void setSourceValue(String sourceValue) { this.sourceValue = sourceValue; }
}
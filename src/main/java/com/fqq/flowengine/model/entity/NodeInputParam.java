package com.fqq.flowengine.model.entity;

import javax.persistence.*;

/**
 * 环节入参配置实体类
 * 对应表：node_input_param
 */
@Entity
@Table(name = "node_input_param")
public class NodeInputParam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_input_param")
    @SequenceGenerator(name = "seq_input_param", sequenceName = "seq_input_param_id", allocationSize = 1)
    private Long id;

    @Column(name = "node_id", nullable = false, length = 50)
    private String nodeId;

    @Column(name = "param_name", nullable = false, length = 100)
    private String paramName;

    @Column(name = "param_source", nullable = false, length = 20)
    private String paramSource;  // request, context, constant

    @Column(name = "source_expression", columnDefinition = "CLOB")
    private String sourceExpression;  // 表达式，如 ${SQ01_out.field} 或常量值

    @Column(name = "is_required", nullable = false)
    private Boolean required = false;

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

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamSource() {
        return paramSource;
    }

    public void setParamSource(String paramSource) {
        this.paramSource = paramSource;
    }

    public String getSourceExpression() {
        return sourceExpression;
    }

    public void setSourceExpression(String sourceExpression) {
        this.sourceExpression = sourceExpression;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
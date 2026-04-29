package com.fqq.flowengine.model.entity;

import javax.persistence.*;

/**
 * 环节出参配置实体类
 * 对应表：node_output_param
 */
@Entity
@Table(name = "node_output_param")
public class NodeOutputParam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_output_param")
    @SequenceGenerator(name = "seq_output_param", sequenceName = "seq_output_param_id", allocationSize = 1)
    private Long id;

    @Column(name = "node_id", nullable = false, length = 50)
    private String nodeId;

    @Column(name = "param_name", nullable = false, length = 100)
    private String paramName;

    @Column(name = "param_value_expression", columnDefinition = "CLOB")
    private String paramValueExpression;  // 从处理器结果中提取值的表达式，如 $.data.userId

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

    public String getParamValueExpression() {
        return paramValueExpression;
    }

    public void setParamValueExpression(String paramValueExpression) {
        this.paramValueExpression = paramValueExpression;
    }
}
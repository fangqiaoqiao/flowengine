package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotBlank;

/**
 * 出入参配置数据传输对象（统一用于入参和出参）
 */
public class ParamDto {

    private Long id;

    @NotBlank(message = "nodeId cannot be blank")
    private String nodeId;

    @NotBlank(message = "paramName cannot be blank")
    private String paramName;

    // 以下字段用于入参配置
    private String paramSource;     // request, context, constant
    private String sourceExpression;
    private Boolean required = false;

    // 以下字段用于出参配置
    private String paramValueExpression;

    public ParamDto() {
    }

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

    public String getParamValueExpression() {
        return paramValueExpression;
    }

    public void setParamValueExpression(String paramValueExpression) {
        this.paramValueExpression = paramValueExpression;
    }
}
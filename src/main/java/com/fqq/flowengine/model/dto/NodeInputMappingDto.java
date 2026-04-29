package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NodeInputMappingDto {

    private Long id;

    @NotBlank(message = "环节ID不能为空")
    @Size(max = 50, message = "环节ID长度不超过50")
    private String nodeId;

    @NotNull(message = "服务入参规范ID不能为空")
    private Long serviceInputParamId;

    @NotBlank(message = "取值类型不能为空")
    @Size(max = 30, message = "取值类型长度不超过30")
    private String sourceType;   // constant, current_input, context_field

    @Size(max = 500, message = "取值内容长度不超过500")
    private String sourceValue;   // 常量值、字段名或上下文表达式

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

    public Long getServiceInputParamId() {
        return serviceInputParamId;
    }

    public void setServiceInputParamId(Long serviceInputParamId) {
        this.serviceInputParamId = serviceInputParamId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }
}
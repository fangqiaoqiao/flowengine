package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ServiceOutputParamDto {

    private Long id;

    private Long serviceId;   // 可选

    @NotBlank(message = "参数名不能为空")
    @Size(max = 100, message = "参数名长度不超过100")
    private String paramName;

    @Size(max = 50)
    private String paramType;

    @Size(max = 200, message = "描述长度不超过200")
    private String description;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() { return paramType; }
    public void setParamType(String paramType) { this.paramType = paramType; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 组件数据传输对象
 */
public class ComponentDto {

    private Long id;

    @NotBlank(message = "componentId cannot be blank")
    @Size(max = 50, message = "componentId max length 50")
    private String componentId;

    @NotBlank(message = "name cannot be blank")
    @Size(max = 100, message = "name max length 100")
    private String name;

    @Size(max = 500, message = "description max length 500")
    private String description;

    // 可含时间字段，按需增加，此处省略

    public ComponentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
package com.fqq.flowengine.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "service_output_param")
public class ServiceOutputParam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sop")
    @SequenceGenerator(name = "seq_sop", sequenceName = "seq_sop_id", allocationSize = 1)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "param_name", nullable = false, length = 100)
    private String paramName;

    @Column(name = "param_type", nullable = false, length = 100)
    private String paramType;

    @Column(length = 200)
    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getParamName() { return paramName; }
    public void setParamName(String paramName) { this.paramName = paramName; }

    public String getParamType() { return paramType; }
    public void setParamType(String paramType) { this.paramType = paramType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
package com.fqq.flowengine.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class ServiceDefDto {

    private Long id;

    @NotBlank(message = "服务编码不能为空")
    @Size(max = 50, message = "服务编码长度不超过50")
    private String serviceCode;

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称长度不超过100")
    private String serviceName;

    @Size(max = 500, message = "描述长度不超过500")
    private String description;

    private String protocol;   // HTTP, DUBBO 等，默认 HTTP

    @Size(max = 500, message = "URL长度不超过500")
    private String url;

    @Size(max = 10, message = "方法长度不超过10")
    private String method;     // GET, POST

    private String headers;    // JSON 格式的请求头

    private List<ServiceInputParamDto> inputParams;   // 服务入参列表
    private List<ServiceOutputParamDto> outputParams; // 服务出参列表

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public List<ServiceInputParamDto> getInputParams() {
        return inputParams;
    }

    public void setInputParams(List<ServiceInputParamDto> inputParams) {
        this.inputParams = inputParams;
    }

    public List<ServiceOutputParamDto> getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(List<ServiceOutputParamDto> outputParams) {
        this.outputParams = outputParams;
    }
}
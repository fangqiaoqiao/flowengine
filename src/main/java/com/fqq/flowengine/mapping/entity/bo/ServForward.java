package com.fqq.flowengine.mapping.entity.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqq.flowengine.mapping.entity.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class ServForward {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TemplateHandle templateHandle;

    /**
     * 执行业务逻辑处理流程
     *
     * @param firstCode  第一个业务编码
     * @param secondCode 第二个业务编码
     * @param inMap      输入参数映射表
     * @return 处理后的输出参数字符串
     */
    public String execute(String firstCode, String secondCode, Map<String, Object> inMap) {
        // 读取请求URL地址
        String url = readUrl(firstCode, secondCode);
        log.info("{}/{};url={}", firstCode, secondCode, url);

        // 转换输入参数格式
        String inParam = convertInParam(firstCode, secondCode, inMap);
        log.info("{}/{};inParam={}", firstCode, secondCode, inParam);

        // 执行远程调用
        String rsp = doCall(url, inParam);
        log.info("{}/{};rsp={}", firstCode, secondCode, rsp);

        return convertOutParam(firstCode, secondCode, rsp);
    }

    /**
     * 发送POST请求到指定URL
     *
     * @param url     请求的URL地址
     * @param inParam 请求体中的参数
     * @return 响应体内容，如果请求成功返回响应内容，否则返回空字符串
     */
    private String doCall(String url, String inParam) {

        // 设置请求头，指定内容类型为JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构造HTTP请求实体，包含请求参数和请求头
        HttpEntity<String> request = new HttpEntity<>(inParam, headers);

        // 发送POST请求并获取响应
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // 检查响应状态码，如果为200则返回响应体内容
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return "";
    }

    /**
     * 读取demo输出参数文件内容
     *
     * @param firstCode 第一编码参数，用于定位文件路径
     * @param secondCode 第二编码参数，用于定位文件路径
     * @return 返回读取到的文件内容字符串
     */
    public String readDemoOutParam(String firstCode, String secondCode) {
        return templateHandle.read(firstCode, secondCode, FileTypeEnum.demoOutParam);
    }


    /**
     * 读取URL配置信息
     *
     * @param firstCode  一级编码
     * @param secondCode 二级编码
     * @return 返回读取到的URL字符串
     */
    public String readUrl(String firstCode, String secondCode) {
        return templateHandle.read(firstCode, secondCode, FileTypeEnum.url);
    }

    /**
     * 将Map转换为输入参数字符串
     *
     * @param firstCode  第一层编码
     * @param secondCode 第二层编码
     * @param inMap      输入参数Map
     * @return 转换后的参数字符串，转换失败时返回空字符串
     */
    private String convertInParam(String firstCode, String secondCode, Map<String, Object> inMap) {
        // 根据编码获取参数模板
        String inParamModel = templateHandle.read(firstCode, secondCode, FileTypeEnum.inParam);
        if (StringUtils.hasLength(inParamModel)) {
            // 使用模板处理参数映射
            return TemplateHandle.mapping(inParamModel, inMap);
        }
        // 模板不存在时，直接将Map序列化为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(inMap);
        } catch (Exception e) {
            log.error("【mapToInParam】【ObjectMapper.writeValueAsString】异常：", e);
        }
        return "";
    }

    /**
     * 将响应数据根据模板映射为输出参数格式
     *
     * @param firstCode    一级编码，用于定位模板文件目录
     * @param secondCode   二级编码，用于定位具体模板文件
     * @param responseJson 原始响应JSON数据
     * @return 映射后的输出参数JSON字符串，如果无对应模板则返回原始响应数据
     */
    private String convertOutParam(String firstCode, String secondCode, String responseJson) {
        // 根据编码获取参数模板
        String outParamModel = templateHandle.read(firstCode, secondCode, FileTypeEnum.outParam);
        if (StringUtils.hasLength(outParamModel)) {
            // 使用模板处理参数映射
            return TemplateHandle.mapping(outParamModel, responseJson);
        }
        return responseJson;
    }

}

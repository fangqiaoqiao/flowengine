package com.fqq.flowengine.mapping.entity.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqq.flowengine.mapping.entity.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Component
public class TemplateHandle {

    @Value("${business.workspace.root}")
    private String rootDir;

    @Value("${business.workspace.serv.mapping}")
    private String mappingDir;

    /**
     * 将指定路径下的文件映射为参数字符串
     *
     * @param firstCode  第一级子目录名称
     * @param secondCode 第二级子目录名称
     * @param type       文件类型标识符，0表示目录路径，其他值对应特定文件名
     * @return 文件内容字符串，如果文件不存在则返回空字符串
     */
    public String read(String firstCode, String secondCode, FileTypeEnum type) {

        Path path = Paths.get(rootDir, mappingDir, firstCode, secondCode, type.toString());

        // 检查路径是否存在，存在则读取文件内容
        if (Files.exists(path)) {
            StringBuilder sb = new StringBuilder();
            try (Stream<String> stream = Files.lines(path)) {
                stream.forEach(sb::append);
            } catch (Exception e) {
                log.error("mapToInParam异常", e);
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 将JSON字符串参数映射到模板中
     *
     * @param template 模板字符串
     * @param param    JSON格式的参数字符串
     * @return 映射后的结果字符串
     */
    public static String mapping(String template, String param) {
        // 将JSON字符串转换为Map对象
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 转map
            Map<String, Object> map = objectMapper.readValue(param, Map.class);
            // 调用重载方法进行模板映射
            return mapping(template, map);
        } catch (Exception e) {
            log.error("【TemplateHandle】【mapping】异常", e);
        }
        return "";
    }

    /**
     * 根据模板和参数映射生成最终字符串
     * 该函数使用正则表达式匹配模板中的占位符，格式为$(变量名)，并用参数Map中对应的值进行替换
     *
     * @param template 模板字符串，包含$(变量名)格式的占位符
     * @param param    参数映射，键为变量名，值为要替换的内容
     * @return 替换后的字符串，未找到对应值的占位符将保持原样
     */
    public static String mapping(String template, Map<String, Object> param) {
        // 编译正则表达式模式，用于匹配$(变量名)格式的占位符
        Pattern pattern = Pattern.compile("\\$\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        // 遍历所有匹配的占位符并进行替换处理
        while (matcher.find()) {
            String variableName = matcher.group(1); // 获取变量名
            String replacement = createValue(variableName, param);

            // 如果找到了对应的值，则替换；否则保留原占位符
            if (replacement != null) {
                // 对特殊字符进行转义，避免正则表达式问题
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                // 没有找到对应的变量，保留原占位符
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        // 将剩余未匹配的部分添加到结果中
        matcher.appendTail(result);
        return result.toString();
    }


    /**
     * 根据键路径从参数映射中获取值并转换为字符串
     *
     * @param key   键路径，使用点号分隔，例如 "user.name" 表示获取嵌套map中的user对象的name属性
     * @param param 包含数据的映射表
     * @return 根据键路径获取到的值转换成的字符串，如果获取不到则返回"null"
     */
    private static String createValue(String key, Map<String, Object> param) {
        // 按照点号分割键路径
        String[] keyArr = key.split("\\.");
        Object value = param;

        // 逐级获取嵌套map中的值
        for (String k : keyArr) {
            value = ((Map<String, Object>) value).get(k);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将最终获取到的值转换为字符串
            return value instanceof String ? String.valueOf(value) : objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("【mapToInParam】【ObjectMapper.writeValueAsString】异常：", e);
        }
        return String.valueOf(value);
    }
}

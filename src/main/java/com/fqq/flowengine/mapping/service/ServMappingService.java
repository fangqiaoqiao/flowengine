package com.fqq.flowengine.mapping.service;

import com.fqq.flowengine.mapping.entity.bo.ServForward;
import com.fqq.flowengine.utils.DownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

@Service
@Slf4j
public class ServMappingService {

    @Resource
    private ServForward servForward;

    /**
     * 映射输入参数到输出参数
     *
     * @param firstCode  第一编码
     * @param secondCode 第二编码
     * @param inMap      输入参数映射表
     * @return 输出参数字符串，如果无法生成则返回空字符串
     */
    public ResponseEntity<?> mapToOutParam(String firstCode, String secondCode, Map<String, Object> inMap) {

        // 读取示例输出参数模板
        String demoOutParam = servForward.readDemoOutParam(firstCode, secondCode);
        log.info("{}/{};demoOutParam={}", firstCode, secondCode, demoOutParam);
        if (StringUtils.hasLength(demoOutParam)) {
            return new ResponseEntity<>(demoOutParam, HttpStatus.OK);
        }

        // 获取请求URL并根据URL类型处理
        String url = servForward.readUrl(firstCode, secondCode);
        // 如果输出参数模板不存在，则尝试通过URL请求获取
        if (url.equals("download")) {
            return downloadFile(String.valueOf(inMap.get("filePath")));
        }

        return requestServ(firstCode, secondCode, inMap);
    }


    /**
     * 执行服务请求转发
     *
     * @param firstCode  第一级服务编码
     * @param secondCode 第二级服务编码
     * @param inMap      输入参数映射
     * @return 包含输出参数的响应实体
     */
    private ResponseEntity<String> requestServ(String firstCode, String secondCode, Map<String, Object> inMap) {
        // 执行服务转发并获取输出参数
        String outParam = servForward.execute(firstCode, secondCode, inMap);
        log.info("{}/{};outParam={}", firstCode, secondCode, outParam);
        return new ResponseEntity<>(outParam, HttpStatus.OK);
    }

    /**
     * 下载指定路径的文件
     *
     * @param filePath 文件在服务器上的完整路径
     * @return ResponseEntity 包含文件字节数据和HTTP响应头的响应实体，
     * 如果文件存在则返回200状态码和文件数据，
     * 如果文件不存在则返回404状态码
     */
    private ResponseEntity<byte[]> downloadFile(String filePath) {
        // 读取文件内容为字节数组
        byte[] fileBytes = DownloadUtil.readFileToByteArraySafe(filePath);
        if (fileBytes == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 获取文件名
        String fileName = new File(filePath).getName();
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(fileBytes.length);
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

}

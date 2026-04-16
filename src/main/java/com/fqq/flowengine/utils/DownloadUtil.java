package com.fqq.flowengine.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class DownloadUtil {

    /**
     * 读取文件到byte数组
     * @param filePath 文件路径
     * @return 文件内容的字节数组
     * @throws Exception 当文件不存在、无权限访问或读取失败时抛出异常
     */
    public static byte[] readFileToByteArray(String filePath) throws Exception {
        // 安全检查
        validateFilePath(filePath);
        return Files.readAllBytes(Paths.get(filePath));
    }


    /**
     * 安全地将文件读取为字节数组
     *
     * @param filePath 文件路径，不能为空或无效路径
     * @return 文件内容的字节数组，读取失败时返回null
     */
    public static byte[] readFileToByteArraySafe(String filePath) {
        validateFilePath(filePath);
        File file = new File(filePath);
        // 使用try-with-resources确保资源正确关闭
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            // 循环读取文件内容到缓冲区，并写入到字节数组输出流
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("文件读取异常，filePath={}", filePath, e);
        }
        return null;
    }


    /**
     * 文件路径安全检查
     */
    private static void validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 防止路径遍历攻击
        if (filePath.contains("..") || filePath.contains("~")) {
            throw new IllegalArgumentException("非法文件路径: " + filePath);
        }

        File file = new File(filePath);

        if (!file.exists()) {
            log.warn("文件不存在: {}", filePath);
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }

        if (!file.isFile()) {
            log.warn("路径不是文件: {}", filePath);
            throw new IllegalArgumentException("路径不是文件: " + filePath);
        }

        if (!file.canRead()) {
            log.warn("文件不可读: {}", filePath);
            throw new IllegalArgumentException("文件不可读: " + filePath);
        }

        // 文件大小限制检查（例如限制为10MB）
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.length() > maxSize) {
            log.warn("文件过大: {} (大小: {} bytes)", filePath, file.length());
            throw new IllegalArgumentException("文件大小超过限制: " + file.length() + " bytes");
        }
    }


}

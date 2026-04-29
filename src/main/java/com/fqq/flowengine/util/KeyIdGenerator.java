package com.fqq.flowengine.util;

import java.util.UUID;

/**
 * 唯一Key生成器
 */
public class KeyIdGenerator {

    private KeyIdGenerator() {
        // 工具类私有构造函数
    }

    /**
     * 生成UUID（去除连字符，减少长度）
     * @return 例如：a1b2c3d4e5f67890
     */
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的KeyId（可用于调试）
     * @param prefix 前缀，如"flow"
     * @return 如"flow_a1b2c3d4e5f67890"
     */
    public static String generateWithPrefix(String prefix) {
        return prefix + "_" + generate();
    }
}
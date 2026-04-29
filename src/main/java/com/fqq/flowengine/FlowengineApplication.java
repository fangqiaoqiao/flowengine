package com.fqq.flowengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * FlowEngine 应用启动类
 *
 * @author fqq
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching        // 启用Spring Cache抽象（可选，与RedisTemplate并存，不影响核心功能）
@EnableJpaRepositories(basePackages = "com.fqq.flowengine.repository")
public class FlowengineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowengineApplication.class, args);
    }
}
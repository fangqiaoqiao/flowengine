package com.fqq.flowengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger3) 配置类
 * - 提供API文档信息
 *
 * @author fqq
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlowEngine API 文档")
                        .version("1.0.0")
                        .description("流程转发引擎REST接口，支持组件/环节管理、场景入口、环节执行")
                        .contact(new Contact()
                                .name("fqq")
                                .email("fqq@example.com")
                                .url("https://github.com/fqq"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}
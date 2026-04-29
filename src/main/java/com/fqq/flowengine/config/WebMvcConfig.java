package com.fqq.flowengine.config;

import com.fqq.flowengine.interceptor.KeyIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * - 跨域配置
 * - 注册拦截器（可选，用于全局校验keyId）
 *
 * @author fqq
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private KeyIdInterceptor keyIdInterceptor;

    /**
     * 跨域配置：允许所有来源（生产环境需按需收紧）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * 注册拦截器（如果keyIdInterceptor Bean存在）
     * 拦截需要校验keyId的路径：/flowengine/service/**
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可选：对环节执行接口进行 keyId 统一校验
        registry.addInterceptor(keyIdInterceptor)
                .addPathPatterns("/flowengine/service/**");
    }
}
package com.fqq.flowengine.mapping.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FlowEngineConf {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

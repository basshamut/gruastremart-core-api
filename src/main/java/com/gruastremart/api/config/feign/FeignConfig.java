package com.gruastremart.api.config.feign;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Client feignClient() {
        return new feign.httpclient.ApacheHttpClient();
    }
}
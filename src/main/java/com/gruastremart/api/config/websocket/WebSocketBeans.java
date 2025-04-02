package com.gruastremart.api.config.websocket;

import com.gruastremart.api.config.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketBeans {

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        return new WebSocketAuthInterceptor(jwtTokenProvider);
    }
}


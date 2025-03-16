package com.gruastremart.api.config.security;

import static com.gruastremart.api.utils.Constants.LOGIN_URL;
import static com.gruastremart.api.utils.Constants.SEND_EMAIL_URL;
import static com.gruastremart.api.utils.Constants.SEND_CONTACTFORM_URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.gruastremart.api.config.security.jwt.JwtSupabaseSecurityFilter;
import com.gruastremart.api.exception.MvcRequestMatcherConfigurationException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityProperties securityProperties;

    private final String[] WHITE_LIST = {
            "/swagger*/**",
            "/v3/api-docs/**",
            "/console/**",
            "/error",
            LOGIN_URL,
            SEND_EMAIL_URL,
            SEND_CONTACTFORM_URL
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    try {
                        for (String pattern : WHITE_LIST) {
                            auth.requestMatchers(new MvcRequestMatcher(introspector, pattern)).permitAll();
                        }
                    } catch (Exception e) {
                        throw new MvcRequestMatcherConfigurationException("Failed to configure MVC request matchers",
                                e);
                    }
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new JwtSupabaseSecurityFilter(securityProperties.getSupabaseSecret()),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
                        
        return http.build();
    }
}

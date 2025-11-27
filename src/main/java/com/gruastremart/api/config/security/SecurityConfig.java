package com.gruastremart.api.config.security;

import com.gruastremart.api.config.security.jwt.JwtSecurityFilter;
import com.gruastremart.api.config.security.jwt.JwtTokenProvider;
import com.gruastremart.api.exception.MvcRequestMatcherConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static com.gruastremart.api.utils.constants.Constants.ACTUATOR_PATH;
import static com.gruastremart.api.utils.constants.Constants.ACTUATOR_PATHS;
import static com.gruastremart.api.utils.constants.Constants.ERROR_PATH;
import static com.gruastremart.api.utils.constants.Constants.FORGOT_PASSWORD_URL;
import static com.gruastremart.api.utils.constants.Constants.H2_CONSOLE_PATH;
import static com.gruastremart.api.utils.constants.Constants.LOGIN_URL;
import static com.gruastremart.api.utils.constants.Constants.REGISTER_FORM_URL;
import static com.gruastremart.api.utils.constants.Constants.RESET_PASSWORD_URL;
import static com.gruastremart.api.utils.constants.Constants.SEND_CONTACTFORM_URL;
import static com.gruastremart.api.utils.constants.Constants.SEND_EMAIL_URL;
import static com.gruastremart.api.utils.constants.Constants.SWAGGER_API_DOCS_PATH;
import static com.gruastremart.api.utils.constants.Constants.SWAGGER_PATH;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private static final String[] MVC_WHITE_LIST = {
            SWAGGER_PATH,
            SWAGGER_API_DOCS_PATH,
            H2_CONSOLE_PATH,
            ERROR_PATH,
            ACTUATOR_PATH,
            LOGIN_URL,
            SEND_EMAIL_URL,
            SEND_CONTACTFORM_URL,
            REGISTER_FORM_URL,
            FORGOT_PASSWORD_URL,
            RESET_PASSWORD_URL,
            ACTUATOR_PATHS
    };

    private static final String[] NON_MVC_WHITE_LIST = {
            "/ws/**" // WebSocket SockJS endpoint
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    try {
                        // Permitir rutas MVC con MvcRequestMatcher (RestControllers)
                        for (String pattern : MVC_WHITE_LIST) {
                            auth.requestMatchers(new MvcRequestMatcher(introspector, pattern)).permitAll();
                        }

                        // Permitir rutas no MVC con AntPathRequestMatcher (WebSocket)
                        for (String pattern : NON_MVC_WHITE_LIST) {
                            auth.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll();
                        }
                    } catch (Exception e) {
                        throw new MvcRequestMatcherConfigurationException("Failed MVC request matchers", e);
                    }

                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new JwtSecurityFilter(jwtTokenProvider),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

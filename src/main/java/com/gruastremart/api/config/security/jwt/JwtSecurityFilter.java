package com.gruastremart.api.config.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtSecurityFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtSecurityFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtSecurityFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authorizationHeader = httpRequest.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String token = authorizationHeader.substring(7);
            Authentication authToken = jwtTokenProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (JwtException e) {
            logger.error("Error al verificar el token JWT de Supabase con JJWT", e);
        } catch (Exception ex) {
            logger.error("Excepción en el filtro de seguridad", ex);
        }

        chain.doFilter(request, response);
    }
}
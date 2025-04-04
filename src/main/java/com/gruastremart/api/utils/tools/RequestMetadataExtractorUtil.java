package com.gruastremart.api.utils.tools;

import com.gruastremart.api.dto.RequestMetadataDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RequestMetadataExtractorUtil {

    private static String jwtSecret;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${app.security.supabaseSecret}")
    public void setJwtSecret(String secret) {
        jwtSecret = secret;
    }

    public static RequestMetadataDto extract(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = authentication.getName();
        var principal = authentication.getPrincipal().toString();

        var claims = extractClaimsFromToken(request);
        var role = claims.getOrDefault("role", "USER").toString();

        var ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        var userAgent = request.getHeader("User-Agent");
        var timestamp = LocalDateTime.now();

        return new RequestMetadataDto(userId, principal, role, ip, userAgent, timestamp);
    }

    private static Claims extractClaimsFromToken(HttpServletRequest request) {
        var token = request.getHeader("Authorization");

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            throw new IllegalStateException("Token JWT no encontrado o inválido");
        }

        token = token.replace(TOKEN_PREFIX, "");

        var decodedKey = jwtSecret.getBytes();

        return Jwts.parser()
                .setSigningKey(decodedKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
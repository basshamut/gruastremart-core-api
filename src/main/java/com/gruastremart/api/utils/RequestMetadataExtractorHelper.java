package com.gruastremart.api.utils;

import com.gruastremart.api.dto.RequestMetadataDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestMetadataExtractorHelper {

    private RequestMetadataExtractorHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static RequestMetadataDto extract(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (UserDetails) authentication.getPrincipal();
        Object details = authentication.getDetails();
        Map<String, Object> claims = extractClaims(details);

        String email = userDetails.getUsername();
        String userId = authentication.getName();
        String role = claims.getOrDefault("role", "USER").toString();

        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        String userAgent = request.getHeader("User-Agent");
        LocalDateTime timestamp = LocalDateTime.now();

        return new RequestMetadataDto(userId, email, role, ip, userAgent, timestamp);
    }

    private static Map<String, Object> extractClaims(Object details) {
        if (details instanceof Map<?, ?> map) {
            Map<String, Object> claims = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    claims.put((String) entry.getKey(), entry.getValue());
                }
            }
            return claims;
        } else {
            throw new IllegalStateException("Authentication details are not a Map");
        }
    }
}
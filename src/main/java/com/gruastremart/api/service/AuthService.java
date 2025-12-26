package com.gruastremart.api.service;

import com.gruastremart.api.client.SupabaseAuthClient;
import com.gruastremart.api.config.security.SecurityProperties;
import com.gruastremart.api.service.EmailService;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SupabaseAuthClient authClient;
    private final SecurityProperties securityProperties;
    private final EmailService emailService;

    @Value("${redirect.forgot-password-url}")
    private String redirectTo;

    public void changePassword(String accessToken, String newPassword) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token requerido para changePassword");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser vacía");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("password", newPassword);

        try {
            authClient.patchUserPassword(securityProperties.getSupabaseAnonKey(), bearer(accessToken), body);
            
            // Enviar email de notificación si el cambio fue exitoso
            try {
                String userEmail = extractEmailFromToken(accessToken);
                if (userEmail != null && !userEmail.equals("unknown")) {
                    sendPasswordChangeNotification(userEmail);
                }
            } catch (Exception emailEx) {
                // Log del error pero no fallar el cambio de contraseña
                System.err.println("Error al enviar email de notificación: " + emailEx.getMessage());
            }
            
        } catch (FeignException e) {
            String msg = safeMessage(e);
            
            // Manejo específico para errores de Supabase
            if (e.status() == 422) {
                String content = getErrorContent(e);
                if (content != null && content.contains("same_password")) {
                    throw new RuntimeException("La nueva contraseña debe ser diferente a la contraseña actual", e);
                }
                if (content != null && content.contains("weak_password")) {
                    throw new RuntimeException("La contraseña no cumple con los requisitos de seguridad", e);
                }
                throw new RuntimeException("Error de validación: " + msg, e);
            }
            
            // 401 → token inválido/expirado; 400 → body inválido, etc.
            throw new RuntimeException("Error al cambiar la contraseña: " + msg, e);
        }
    }

    public void forgotPassword(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        
        // Agregar callback URL si se proporciona
        if (redirectTo != null && !redirectTo.isBlank()) {
            body.put("redirect_to", redirectTo);
        }

        try {
            authClient.recover(securityProperties.getSupabaseAnonKey(), body);
        } catch (FeignException e) {
            if (e.status() != 401) {
                throw new RuntimeException("Error al iniciar recuperación de contraseña: " + buildErrorMessage(e), e);
            }
        }
    }

    public void resetPassword(String recoveryAccessToken, String newPassword) {
        // En el flujo de Supabase, tras el enlace de email, recibirás un access_token de recuperación.
        if (recoveryAccessToken == null || recoveryAccessToken.isBlank()) {
            throw new IllegalArgumentException("Recovery access token requerido para resetPassword");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser vacía");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("password", newPassword);

        try {
            authClient.patchUserPassword(securityProperties.getSupabaseSecret(), bearer(recoveryAccessToken), body);
        } catch (FeignException e) {
            String msg = safeMessage(e);
            throw new RuntimeException("Error al restablecer la contraseña: " + msg, e);
        }
    }

    private static String bearer(String token) {
        String t = token.trim();
        if (t.toLowerCase().startsWith("bearer ")) {
            return t; // ya viene con prefijo
        }
        return "Bearer " + t;
    }

    private static String safeMessage(FeignException e) {
        try {
            if (e.content() != null && e.content().length > 0) {
                String content = new String(e.content(), StandardCharsets.UTF_8);
                if (!content.trim().isEmpty()) {
                    return sanitizeErrorMessage(content);
                }
            }
        } catch (Exception ignore) {
            // Ignorar errores de parsing
        }

        // Si no hay contenido útil, devolver información del status y mensaje sanitizado
        String message = e.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            return sanitizeErrorMessage(message);
        }

        return "Error HTTP " + e.status() + " - Sin detalles adicionales";
    }

    private static String sanitizeErrorMessage(String message) {
        if (message == null) return null;
        
        // Ocultar URLs de Supabase que contengan información sensible
        return message.replaceAll("https://[a-zA-Z0-9]+\\.supabase\\.co", "https://*****.supabase.co")
                     .replaceAll("supabase\\.co/[^\\s]+", "supabase.co/****");
    }

    private String extractEmailFromToken(String accessToken) {
        try {
            String token = accessToken.trim();
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7);
            }
            
            Key signingKey = Keys.hmacShaKeyFor(securityProperties.getSupabaseSecret().getBytes());
            
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.containsKey("email") ? claims.get("email").toString() : "unknown";
        } catch (Exception e) {
            System.err.println("Error al extraer email del token: " + e.getMessage());
            return "unknown";
        }
    }
    
    private void sendPasswordChangeNotification(String userEmail) {
        try {
            emailService.sendPasswordChangeNotification(userEmail);
        } catch (Exception e) {
            System.err.println("Error al enviar notificación de cambio de contraseña: " + e.getMessage());
        }
    }

    private static String getErrorContent(FeignException e) {
        try {
            if (e.content() != null && e.content().length > 0) {
                return new String(e.content(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignore) {
            // Ignorar errores de parsing
        }
        return null;
    }

    private static String buildErrorMessage(FeignException e) {
        String body = null;
        try {
            if (e.content() != null) body = new String(e.content(), StandardCharsets.UTF_8);
        } catch (Exception ignore) {
        }
        
        String sanitizedMessage = sanitizeErrorMessage(e.getMessage());
        String sanitizedBody = body != null ? sanitizeErrorMessage(body) : null;
        
        return "status=" + e.status() + ", reason=" + sanitizedMessage + 
               (sanitizedBody != null ? (", body=" + sanitizedBody) : "");
    }
}
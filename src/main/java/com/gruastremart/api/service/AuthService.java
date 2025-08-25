package com.gruastremart.api.service;

import com.gruastremart.api.client.SupabaseAuthClient;
import com.gruastremart.api.config.security.SecurityProperties;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SupabaseAuthClient authClient;

    private final SecurityProperties securityProperties;

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
        } catch (FeignException e) {
            String msg = safeMessage(e);
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

        try {
            authClient.recover(securityProperties.getSupabaseAnonKey(), body);
            return;
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
                    return content;
                }
            }
        } catch (Exception ignore) {
            // Ignorar errores de parsing
        }

        // Si no hay contenido útil, devolver información del status y mensaje
        String message = e.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            return message;
        }

        return "Error HTTP " + e.status() + " - Sin detalles adicionales";
    }

    private static String buildErrorMessage(FeignException e) {
        String body = null;
        try {
            if (e.content() != null) body = new String(e.content(), StandardCharsets.UTF_8);
        } catch (Exception ignore) {
        }
        return "status=" + e.status() + ", reason=" + e.getMessage() + (body != null ? (", body=" + body) : "");
    }
}
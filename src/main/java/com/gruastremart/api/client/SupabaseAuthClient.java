package com.gruastremart.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Cliente Feign para endpoints de Autenticación (GoTrue) en Supabase.
 */
@FeignClient(
        name = "supabaseAuthClient",
        url = "${app.security.supabaseUrl}"
)
public interface SupabaseAuthClient {

    /**
     * Inicia proceso de recuperación de contraseña (envía email con enlace).
     * POST /auth/v1/recover
     * Body: { "email": "...", "redirect_to": "https://..." }
     * Headers: apikey
     */
    @PostMapping(
            value = "/auth/v1/recover",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void recover(
            @RequestHeader("apikey") String apiKey,
            @RequestBody Map<String, Object> body
    );

    /**
     * Cambia/restablece la contraseña del usuario autenticado por el token (access/recovery).
     * PATCH /auth/v1/user
     * Body: { "password": "..." }
     * Headers: apikey, Authorization: Bearer <token>
     */
    @PatchMapping(
            value = "/auth/v1/user",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void patchUserPassword(
            @RequestHeader("apikey") String apiKey,
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> body
    );
}

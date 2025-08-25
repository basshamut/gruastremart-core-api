package com.gruastremart.api.controller;

import com.gruastremart.api.dto.ChangePasswordRequestDto;
import com.gruastremart.api.dto.ForgotPasswordRequestDto;
import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.dto.ResetPasswordRequestDto;
import com.gruastremart.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(API_VERSION_PATH + "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "API para gestión de autenticación y recuperación de contraseñas")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Change Password", description = "Cambia la contraseña del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente")
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            @RequestHeader("Authorization") @Parameter(description = "Bearer token del usuario autenticado", required = true) String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        authService.changePassword(token, request.getNewPassword());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Forgot Password", description = "Inicia el proceso de recuperación de contraseña enviando un email al usuario")
    @ApiResponse(responseCode = "200", description = "Email de recuperación enviado exitosamente")
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset Password", description = "Restablece la contraseña del usuario usando el token de recuperación")
    @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente")
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok().build();
    }
}

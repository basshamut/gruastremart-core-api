package com.gruastremart.api.controller;

import com.gruastremart.api.dto.PaymentCreateRequestDto;
import com.gruastremart.api.dto.PaymentResponseDto;
import com.gruastremart.api.dto.PaymentVerifyRequestDto;
import com.gruastremart.api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "API para gestión de pagos de servicios de grúa")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Registrar un nuevo pago",
            description = "Registra un pago para una demanda de grúa completada. Requiere imagen del comprobante."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o demanda no completada"),
            @ApiResponse(responseCode = "404", description = "Demanda no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Map<String, Object>> registerPayment(
            @Valid @ModelAttribute PaymentCreateRequestDto dto,
            Authentication authentication
    ) {
        log.info("Solicitud de registro de pago para demanda: {}", dto.getDemandId());

        PaymentResponseDto payment = paymentService.registerPayment(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pago registrado exitosamente");
        response.put("data", payment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Obtener historial de pagos",
            description = "Obtiene el historial de pagos del usuario autenticado con paginación y filtros opcionales"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Page<PaymentResponseDto>> getPaymentHistory(
            @Parameter(description = "ID del usuario") @RequestParam String userId,
            @Parameter(description = "Estado del pago (PENDING, VERIFIED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        log.info("Obteniendo historial de pagos para usuario: {}", userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponseDto> payments = paymentService.getPaymentHistory(userId, status, pageable);

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de un pago",
            description = "Obtiene los detalles completos de un pago específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @Parameter(description = "ID del pago") @PathVariable String id,
            Authentication authentication
    ) {
        log.info("Obteniendo detalles del pago: {}", id);

        PaymentResponseDto payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{id}/verify")
    @Operation(
            summary = "Verificar o rechazar un pago",
            description = "Permite a un administrador verificar o rechazar un pago. Requiere rol de administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol admin)")
    })
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @Parameter(description = "ID del pago") @PathVariable String id,
            @Valid @RequestBody PaymentVerifyRequestDto dto,
            Authentication authentication
    ) {
        log.info("Verificando pago: {} con estado: {}", id, dto.getStatus());

        // Obtener el ID del usuario autenticado (administrador)
        String verifiedByUserId = authentication.getName(); // O extraer del token JWT

        PaymentResponseDto payment = paymentService.verifyPayment(id, dto, verifiedByUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pago actualizado exitosamente");
        response.put("data", payment);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @Operation(
            summary = "Rechazar un pago",
            description = "Permite a un administrador rechazar un pago. Requiere rol de administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago rechazado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol admin)")
    })
    public ResponseEntity<Map<String, Object>> rejectPayment(
            @Parameter(description = "ID del pago") @PathVariable String id,
            @Valid @RequestBody PaymentVerifyRequestDto dto,
            Authentication authentication
    ) {
        log.info("Rechazando pago: {}", id);

        String verifiedByUserId = authentication.getName();

        PaymentResponseDto payment = paymentService.rejectPayment(id, dto, verifiedByUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pago rechazado exitosamente");
        response.put("data", payment);

        return ResponseEntity.ok(response);
    }
}

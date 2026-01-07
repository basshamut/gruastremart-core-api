package com.gruastremart.api.controller;

import com.gruastremart.api.dto.PaymentCreateRequestDto;
import com.gruastremart.api.dto.PaymentResponseDto;
import com.gruastremart.api.dto.PaymentVerifyRequestDto;
import com.gruastremart.api.service.PaymentService;
import com.gruastremart.api.utils.tools.RequestMetadataExtractorUtil;
import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest request
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
            HttpServletRequest request
    ) {
        log.info("Obteniendo historial de pagos para usuario: {}", userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponseDto> payments = paymentService.getPaymentHistory(userId, status, pageable);

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/operator/{operatorId}")
    @Operation(
            summary = "Obtener pagos de un operador",
            description = "Obtiene los pagos de las demandas completadas por un operador específico. Los operadores solo pueden ver sus propios pagos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos del operador obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (solo el operador puede ver sus propios pagos o un administrador)")
    })
    public ResponseEntity<Page<PaymentResponseDto>> getOperatorPayments(
            @Parameter(description = "Estado del pago (PENDING, VERIFIED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        // Obtener el ID del usuario autenticado usando RequestMetadataExtractorUtil
        var meta = RequestMetadataExtractorUtil.extract(request);
        String authenticatedUserEmail = meta.getEmail();
        log.info("Obteniendo pagos para operador: {} )", authenticatedUserEmail);

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponseDto> payments = paymentService.getOperatorPayments(authenticatedUserEmail, status, pageable);

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Obtener todos los pagos del sistema",
            description = "Obtiene todos los pagos del sistema con paginación. Solo disponible para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol de administrador)")
    })
    public ResponseEntity<Page<PaymentResponseDto>> getAllPayments(
            @Parameter(description = "Estado del pago (PENDING, VERIFIED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        // Obtener el ID del usuario autenticado usando RequestMetadataExtractorUtil
        var meta = RequestMetadataExtractorUtil.extract(request);
        String authenticatedUserId = meta.getUserId();
        log.info("Obteniendo todos los pagos (solicitado por: {})", authenticatedUserId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponseDto> payments = paymentService.getAllPayments(status, pageable);

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
            HttpServletRequest request
    ) {
        log.info("Obteniendo detalles del pago: {}", id);

        PaymentResponseDto payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{id}/verify")
    @Operation(
            summary = "Verificar o rechazar un pago",
            description = "Permite a un administrador u operador verificar o rechazar un pago. El operador solo puede verificar pagos de sus propias demandas completadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol admin o ser el operador asignado)")
    })
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @Parameter(description = "ID del pago") @PathVariable String id,
            @Valid @RequestBody PaymentVerifyRequestDto dto,
            HttpServletRequest request
    ) {
        log.info("Verificando pago: {} con estado: {}", id, dto.getStatus());

        // Obtener el ID del usuario autenticado usando RequestMetadataExtractorUtil
        var meta = RequestMetadataExtractorUtil.extract(request);
        String verifiedByUserEmail = meta.getEmail();

        PaymentResponseDto payment = paymentService.verifyPayment(id, dto, verifiedByUserEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pago actualizado exitosamente");
        response.put("data", payment);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @Operation(
            summary = "Rechazar un pago",
            description = "Permite a un administrador u operador rechazar un pago. El operador solo puede rechazar pagos de sus propias demandas completadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago rechazado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol admin o ser el operador asignado)")
    })
    public ResponseEntity<Map<String, Object>> rejectPayment(
            @Parameter(description = "ID del pago") @PathVariable String id,
            @Valid @RequestBody PaymentVerifyRequestDto dto,
            HttpServletRequest request
    ) {
        log.info("Rechazando pago: {}", id);

        var meta = RequestMetadataExtractorUtil.extract(request);
        String verifiedByUserEmail = meta.getEmail();

        PaymentResponseDto payment = paymentService.rejectPayment(id, dto, verifiedByUserEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pago rechazado exitosamente");
        response.put("data", payment);

        return ResponseEntity.ok(response);
    }
}

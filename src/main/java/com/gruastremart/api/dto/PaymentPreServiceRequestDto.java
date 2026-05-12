package com.gruastremart.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPreServiceRequestDto {

    @NotBlank(message = "El ID del usuario es obligatorio")
    private String userId;

    @NotBlank(message = "La referencia de pago móvil es obligatoria")
    private String mobilePaymentReference;

    @NotNull(message = "La imagen del comprobante es obligatoria")
    private MultipartFile paymentImage;

    @NotNull(message = "El monto del pago es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private Double amount;

    // Los datos completos de la solicitud de grúa que se creará al verificar el pago
    @NotNull(message = "Los datos de la solicitud son obligatorios")
    private CraneDemandCreateRequestDto demandData;
}

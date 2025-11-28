package com.gruastremart.api.dto;

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
public class PaymentCreateRequestDto {
    
    @NotBlank(message = "El ID de la demanda es obligatorio")
    private String demandId;
    
    @NotBlank(message = "El ID del usuario es obligatorio")
    private String userId;
    
    @NotBlank(message = "La referencia de pago móvil es obligatoria")
    private String mobilePaymentReference;
    
    @NotNull(message = "La imagen del comprobante es obligatoria")
    private MultipartFile paymentImage;
    
    // Monto opcional para referencia
    private Double amount;
}

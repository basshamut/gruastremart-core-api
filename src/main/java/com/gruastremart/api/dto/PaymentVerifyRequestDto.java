package com.gruastremart.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerifyRequestDto {
    
    @NotBlank(message = "El estado es obligatorio (VERIFIED o REJECTED)")
    private String status;
    
    // Comentarios opcionales del verificador
    private String verificationComments;
}

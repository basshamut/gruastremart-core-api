package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    
    private String id;
    private String demandId;
    private String userId;
    private String mobilePaymentReference;
    private String paymentImageUrl;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Date verifiedAt;
    private String verifiedByUserId;
    private String verificationComments;
    private Double amount;
    
    // Información adicional de la demanda (opcional)
    private String demandOrigin;
    private String demandCarType;
}

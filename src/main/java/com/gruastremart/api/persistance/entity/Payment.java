package com.gruastremart.api.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    private String id;
    
    // Relación con la demanda de grúa
    private String demandId;
    
    // Usuario que realiza el pago
    private String userId;
    
    // Referencia del pago móvil
    private String mobilePaymentReference;
    
    // URL de la imagen del comprobante (almacenada externamente)
    private String paymentImageUrl;
    
    // Estado del pago: PENDING, VERIFIED, REJECTED
    private String status;
    
    // Fechas de auditoría
    private Date createdAt;
    private Date updatedAt;
    private Date verifiedAt;
    
    // Usuario que verificó o rechazó el pago
    private String verifiedByUserId;
    
    // Comentarios del verificador (opcional)
    private String verificationComments;
    
    // Monto del pago (opcional, para referencia)
    private Double amount;
}

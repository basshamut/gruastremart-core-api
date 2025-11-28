package com.gruastremart.api.mapper;

import com.gruastremart.api.dto.PaymentResponseDto;
import com.gruastremart.api.persistance.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    
    /**
     * Convierte una entidad Payment a PaymentResponseDto
     */
    PaymentResponseDto toResponseDto(Payment payment);
    
    /**
     * Convierte un PaymentResponseDto a entidad Payment
     */
    Payment toEntity(PaymentResponseDto paymentResponseDto);
}

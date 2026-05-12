package com.gruastremart.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CraneDemandCreateRequestDto {
    private String description;
    private String origin;
    private String carType;
    private String breakdown;
    private String referenceSource;
    private String recommendedBy;

    // Vehicle information
    private String vehicleBrand;
    private String vehicleModel;
    private Integer vehicleYear;
    private String vehiclePlate;
    private String vehicleColor;

    // Customer contact information
    @NotBlank(message = "Nombre del cliente es requerido")
    private String customerName;

    @NotBlank(message = "Teléfono del cliente es requerido")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido")
    private String customerPhone;

    private LocationDto currentLocation;
    private LocationDto destinationLocation;
}

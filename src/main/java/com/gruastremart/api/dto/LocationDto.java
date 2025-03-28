package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LocationDto {
    private Double latitude;
    private Double longitude;
    private Double accuracy; // Puede ser null si es destino
    private String name;
}

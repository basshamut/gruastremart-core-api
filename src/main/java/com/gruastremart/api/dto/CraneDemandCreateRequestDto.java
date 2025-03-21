package com.gruastremart.api.dto;

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
    private LocationDto currentLocation;
    private LocationDto destinationLocation;
}

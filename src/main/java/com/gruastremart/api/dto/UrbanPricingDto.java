package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrbanPricingDto {
    private String type;
    private Integer maxDistanceKm;
    private Double fixedPriceUsd;
    private String description;
}

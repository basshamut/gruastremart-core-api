package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraUrbanPricingDto {
    private String type;
    private Double basePriceUsd;
    private Double pricePerKmUsd;
    private String description;
}

package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CranePricingResponseDto {
    private String id;
    private WeightCategoryDto weightCategory;
    private PricingDto pricing;
    private Date createdAt;
    private Date updatedAt;
    private Boolean active;
}

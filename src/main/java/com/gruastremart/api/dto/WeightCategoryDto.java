package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightCategoryDto {
    private String name;
    private Integer minWeightKg;
    private Integer maxWeightKg;
    private String description;
}

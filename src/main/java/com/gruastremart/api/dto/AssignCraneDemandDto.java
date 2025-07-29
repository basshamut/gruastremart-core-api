package com.gruastremart.api.dto;

import com.gruastremart.api.utils.enums.WeightCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AssignCraneDemandDto {
    private String userId;
    private WeightCategoryEnum weightCategory;
    private Double latitude;
    private Double longitude;
}

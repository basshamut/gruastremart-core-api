package com.gruastremart.api.dto;

import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CraneDemandUpdateRequestDto {
    private String userId;
    private String description;
    private CraneDemandStateEnum state;
}


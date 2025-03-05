package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CraneDemandUpdateRequestDto {
    private String description;
    private String state;
}


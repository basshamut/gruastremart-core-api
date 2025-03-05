package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CraneDemandResponseDto {
    private String id;
    private String description;
    private String userId;
    private Date dueDate;
    private String state;
}


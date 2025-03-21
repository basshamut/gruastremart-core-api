package com.gruastremart.api.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CraneDemandResponseDto {
    private String id;
    private String description;
    private String userId;
    private Date dueDate;
    private String state;
}

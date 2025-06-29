package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorLocationDto {
    private String operatorId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private String status; // ONLINE, OFFLINE, BUSY
}

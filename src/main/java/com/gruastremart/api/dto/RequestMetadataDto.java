package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequestMetadataDto {
    private String userId;
    private String email;
    private String role;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;
}

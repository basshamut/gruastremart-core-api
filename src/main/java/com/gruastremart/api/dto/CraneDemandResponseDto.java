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
    private Date createdAt;
    private String state;
    private String userId;
    private String assignedOperatorId;
    private String origin;
    private String carType;
    private String breakdown;
    private String referenceSource;
    private String recommendedBy;
    private LocationDto currentLocation;
    private LocationDto destinationLocation;
}

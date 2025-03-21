package com.gruastremart.api.persistance.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "crane_demands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CraneDemand {
    @Id
    private String id;
    private String description;
    private Date dueDate;
    private String state;
    private String userId;
    private String origin;
    private String carType;
    private String breakdown;
    private String referenceSource;
    private String recommendedBy;
    private Location currentLocation;
    private Location destinationLocation;
}

package com.gruastremart.api.persistance.entity;

import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "crane_demands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CraneDemand {
    @Id
    private String id;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String state;
    private String createdByUserId;
    private String editedByUserId;
    private String assignedOperatorId;
    private String origin;
    private String carType;
    private String breakdown;
    private String referenceSource;
    private String recommendedBy;

    // Vehicle information
    private String vehicleBrand;
    private String vehicleModel;
    private Integer vehicleYear;
    private String vehiclePlate;
    private String vehicleColor;

    // Pricing information
    private String assignedWeightCategoryId;
    
    // Payment information
    private String paymentId;

    @GeoSpatialIndexed
    private GeoJsonPoint currentLocation;
    private String currentLocationName;
    private Double currentLocationAccuracy;
    @GeoSpatialIndexed
    private GeoJsonPoint destinationLocation;
    private String destinationLocationName;
    private Double destinationLocationAccuracy;

    public boolean isActiveOrTaken() {
        return this.getState().equals(CraneDemandStateEnum.ACTIVE.name()) || this.getState().equals(CraneDemandStateEnum.TAKEN.name());
    }
}


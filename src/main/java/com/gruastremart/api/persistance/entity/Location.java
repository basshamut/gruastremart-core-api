package com.gruastremart.api.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Location {
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private String name;
}

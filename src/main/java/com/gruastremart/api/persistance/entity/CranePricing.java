package com.gruastremart.api.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "crane_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CranePricing {
    @Id
    private String id;

    @Field("weight_category")
    private WeightCategory weightCategory;

    private Pricing pricing;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    private Boolean active;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeightCategory {
        private String name;

        @Field("min_weight_kg")
        private Integer minWeightKg;

        @Field("max_weight_kg")
        private Integer maxWeightKg;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pricing {
        private UrbanPricing urban;

        @Field("extra_urban")
        private ExtraUrbanPricing extraUrban;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UrbanPricing {
        private String type;

        @Field("max_distance_km")
        private Integer maxDistanceKm;

        @Field("fixed_price_usd")
        private Double fixedPriceUsd;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExtraUrbanPricing {
        private String type;

        @Field("base_price_usd")
        private Double basePriceUsd;

        @Field("price_per_km_usd")
        private Double pricePerKmUsd;

        private String description;
    }
}

package com.gruastremart.api.utils.enums;

import lombok.Getter;

@Getter
public enum WeightCategoryEnum {
    PESO_1("peso_1", "Hasta 2500 kg"),
    PESO_2("peso_2", "De 2501 a 5000 kg"),
    PESO_3("peso_3", "De 5001 a 10000 kg"),
    PESO_4("peso_4", "Más de 10000 kg");

    private final String id;
    private final String description;

    WeightCategoryEnum(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static WeightCategoryEnum fromId(String id) {
        for (WeightCategoryEnum category : values()) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid weight category ID: " + id);
    }
}

package com.gruastremart.api.utils.enums;

public enum CraneDemandStateEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    TAKEN("TAKEN"),
    COMPLETED("COMPLETED");

    private final String state;

    CraneDemandStateEnum(String state) {
        this.state = state;
    }
    }

package com.gruastremart.api.utils.enums;

import lombok.Getter;

@Getter
public enum OperatorVehiculeTypeEnum {
    CRANE("CRANE"),
    TRUCK("TRUCK"),
    TRAILER("TRAILER"),
    FORKLIFT("FORKLIFT"),
    EXCAVATOR("EXCAVATOR"),
    DUMP_TRUCK("DUMP_TRUCK"),
    LOADER("LOADER"),
    BACKHOE_LOADER("BACKHOE_LOADER"),
    GRADER("GRADER"),
    BULLDOZER("BULLDOZER");

    private final String value;

    OperatorVehiculeTypeEnum(String value) {
        this.value = value;
    }

}

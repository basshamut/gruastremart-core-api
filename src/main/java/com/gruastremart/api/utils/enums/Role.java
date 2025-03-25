package com.gruastremart.api.utils.enums;

import lombok.Getter;

@Getter
public enum Role {
    CLIENT("CLIENT"),
    OPERATOR("OPERATOR"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

}

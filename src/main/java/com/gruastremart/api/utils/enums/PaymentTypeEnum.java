package com.gruastremart.api.utils.enums;

public enum PaymentTypeEnum {
    PRE_SERVICE("PRE_SERVICE"),   // Pago antes del servicio (nuevo flujo)
    POST_SERVICE("POST_SERVICE"), // Pago después del servicio (flujo antiguo, retrocompatibilidad)
    ;

    PaymentTypeEnum(String type) {
    }
}

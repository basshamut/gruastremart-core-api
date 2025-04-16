package com.gruastremart.api.exception;

import lombok.Getter;

import java.io.Serial;

public class ServiceException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = 1L;
    private final String message;
    @Getter
    private final Integer code;

	public ServiceException(String message, Integer code) {
        super();
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

package com.btto.core.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{
    private final HttpStatus status;

    public ApiException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiException(final String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

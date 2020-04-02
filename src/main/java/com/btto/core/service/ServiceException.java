package com.btto.core.service;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final Type type;

    public ServiceException(final String message, final Type type) {
        super(message);
        this.type = type;
    }

    public enum Type {
        NOT_FOUND, WRONG_OLD_PASSWORD, ALREADY_EXISTS
    }
}

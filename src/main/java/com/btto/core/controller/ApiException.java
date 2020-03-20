package com.btto.core.controller;

public class ApiException extends RuntimeException{
    public ApiException(String message) {
        super(message);
    }
}

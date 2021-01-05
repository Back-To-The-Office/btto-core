package com.btto.core.controller;

import com.btto.core.controller.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        final ErrorResponse responseBody = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(responseBody, exception.getStatus());
    }
}

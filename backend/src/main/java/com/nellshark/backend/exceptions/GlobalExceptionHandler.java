package com.nellshark.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
        log.error("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());

        ApiError apiError = new ApiError(
                INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}

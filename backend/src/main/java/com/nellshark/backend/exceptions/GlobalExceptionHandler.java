package com.nellshark.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiError> handleGameNotFoundException(
            RuntimeException e, HttpServletRequest request) {
        log.warn("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());

        ApiError apiError = new ApiError(
                NOT_FOUND,
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalServerErrorException(Exception e, HttpServletRequest request) {
        log.error("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
        ApiError apiError = new ApiError(
                INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}

package com.nellshark.backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            GameNotFoundException.class,
            UserNotFoundException.class
    })
    public ProblemDetail handleNotFoundException(RuntimeException e) {
        log.warn("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ProblemDetail handleBadRequestException(RuntimeException e) {
        log.warn("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.warn("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternalServerErrorException(Exception e) {
        log.error("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
        return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
    }
}

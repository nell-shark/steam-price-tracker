package com.nellshark.backend.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NotNull MethodArgumentNotValidException e,
      @NotNull HttpHeaders headers,
      @NotNull HttpStatusCode status,
      @NotNull WebRequest request) {
    super.handleMethodArgumentNotValid(e, headers, status, request);
    log.warn("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());

    String errorMessage = e.getBindingResult().getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(". "));

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, errorMessage);
    return ResponseEntity.of(problemDetail).build();
  }

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


  @ExceptionHandler(Exception.class)
  public ProblemDetail handleInternalServerErrorException(Exception e) {
    log.error("{} Occurred: {}", e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
  }
}

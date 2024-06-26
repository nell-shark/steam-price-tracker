package com.nellshark.backend.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String LOG_OCCURRED_MESSAGE = "{} Occurred: {}";

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    super.handleMethodArgumentNotValid(e, headers, status, request);
    log.warn(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

    String errorMessage = e.getBindingResult().getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(". "));

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, errorMessage);
    return ResponseEntity.of(problemDetail).build();
  }

  @ExceptionHandler({
      AppNotFoundException.class,
      UserNotFoundException.class,
      BadCredentialsException.class
  })
  public ProblemDetail handleNotFoundException(RuntimeException e) {
    log.warn(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler({
      EmailAlreadyTakenException.class,
      ReCaptchaInvalidException.class,
      JwtException.class,
      IllegalArgumentException.class
  })
  public ProblemDetail handleBadRequestException(RuntimeException e) {
    log.warn(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleForbiddenException(RuntimeException e) {
    log.warn(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ProblemDetail handleUnauthorizedException(RuntimeException e) {
    log.warn(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(UNAUTHORIZED, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleInternalServerErrorException(Exception e) {
    log.error(LOG_OCCURRED_MESSAGE, e.getClass().getSimpleName(), e.getMessage());
    return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
  }
}

package com.nellshark.backend.exceptions;

public class RateLimitException extends RuntimeException {

  public RateLimitException(String message, Throwable cause) {
    super(message, cause);
  }
}

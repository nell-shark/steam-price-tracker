package com.nellshark.backend.exceptions;

public class ReCaptchaInvalidException extends RuntimeException {

  public ReCaptchaInvalidException(String message) {
    super(message);
  }
}

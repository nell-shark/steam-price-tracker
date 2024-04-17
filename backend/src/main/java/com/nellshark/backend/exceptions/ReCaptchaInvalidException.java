package com.nellshark.backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class ReCaptchaInvalidException extends AuthenticationException {

  public ReCaptchaInvalidException(String message) {
    super(message);
  }
}

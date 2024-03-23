package com.nellshark.backend.exceptions;

public class AppNotFoundException extends RuntimeException {

  public AppNotFoundException(String message) {
    super(message);
  }
}

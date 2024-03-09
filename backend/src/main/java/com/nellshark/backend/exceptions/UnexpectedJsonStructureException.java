package com.nellshark.backend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UnexpectedJsonStructureException extends RuntimeException {

  public UnexpectedJsonStructureException(String message) {
    super(message);
  }

  public UnexpectedJsonStructureException(String message, JsonProcessingException e) {
    super(message, e);
  }
}

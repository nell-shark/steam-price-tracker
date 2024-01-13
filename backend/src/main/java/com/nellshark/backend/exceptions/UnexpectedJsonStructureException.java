package com.nellshark.backend.exceptions;

public class UnexpectedJsonStructureException extends RuntimeException {
    public UnexpectedJsonStructureException(String message) {
        super(message);
    }
}

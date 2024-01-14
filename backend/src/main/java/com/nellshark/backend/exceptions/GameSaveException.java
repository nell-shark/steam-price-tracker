package com.nellshark.backend.exceptions;

public class GameSaveException extends RuntimeException {
    public GameSaveException(String message) {
        super(message);
    }
}

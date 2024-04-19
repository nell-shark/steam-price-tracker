package com.nellshark.backend.dtos.requests;

public record AuthRequest(
    String email,
    String password
) {

}

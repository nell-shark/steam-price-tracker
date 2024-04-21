package com.nellshark.backend.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
    @JsonProperty("accessToken")
    String accessToken,

    @JsonProperty("refreshToken")
    String refreshToken
) {

}

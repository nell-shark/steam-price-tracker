package com.nellshark.backend.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecaptchaResponse(
    @JsonProperty("success")
    boolean success,

    @JsonProperty("challenge_ts")
    String challengeTs,

    @JsonProperty("hostname")
    String hostname,

    @JsonProperty("error-codes")
    List<String> errorCodes
) {

}

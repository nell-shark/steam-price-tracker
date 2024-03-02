package com.nellshark.backend.responses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.Map;


@Getter
public class AppDetails {
    private App app;

    @JsonAnySetter
    public void handleUnknown(String ignoredKey, Object value) {
        if (value instanceof Map<?, ?> map) {
            ObjectMapper mapper = new ObjectMapper();
            this.app = mapper.convertValue(map, App.class);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record App(boolean success) {
    }
}

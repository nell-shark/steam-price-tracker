package com.nellshark.backend.models.clientresponses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public record App(@JsonProperty("success") boolean success, @JsonProperty("data") Data data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(@JsonProperty("type") String type,
                       @JsonProperty("name") String name,
                       @JsonProperty("steam_appid") long steamAppId,
                       @JsonProperty("is_free") boolean isFree,
                       @JsonProperty("short_description") String shortDescription,
                       @JsonProperty("header_image") String headerImage,
                       @JsonProperty("website") String website,
                       @JsonProperty("metacritic") Metacritic metacritic,
                       @JsonProperty("release_date") ReleaseDate releaseDate) {
    }

    public record Metacritic(@JsonProperty("score") Integer score, @JsonProperty("url") String url) {
    }

    public record ReleaseDate(@JsonProperty("coming_soon") Boolean comingSoon, @JsonProperty("date") String date) {
    }
}

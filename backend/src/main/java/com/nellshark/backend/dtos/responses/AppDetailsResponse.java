package com.nellshark.backend.dtos.responses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class AppDetailsResponse {

  private App app;

  /**
   * Handles unknown properties during deserialization by dynamically mapping them to the 'app'
   * field. The ignored key represents the app id.
   * { "620": { "success": true, "data": { ... } } }
   *
   * @param ignoredKey The numeric ID of the unknown property.
   * @param value      The value associated with the unknown property.
   */
  @JsonAnySetter
  public void handleUnknown(String ignoredKey, Object value) {
    if (value instanceof Map<?, ?> map) {
      ObjectMapper mapper = new ObjectMapper();
      this.app = mapper.convertValue(map, App.class);
    }
  }

  public record App(@JsonProperty("success") boolean success,
                    @JsonProperty("data") Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(@JsonProperty("type") String type,
                       @JsonProperty("name") String name,
                       @JsonProperty("steam_appid") long steamAppId,
                       @JsonProperty("is_free") boolean isFree,
                       @JsonProperty("short_description") String shortDescription,
                       @JsonProperty("header_image") String headerImage,
                       @JsonProperty("website") String website,
                       @JsonProperty("developers") List<String> developers,
                       @JsonProperty("publishers") List<String> publishers,
                       @JsonProperty("price_overview") PriceOverview priceOverview,
                       @JsonProperty("platforms") Platforms platforms,
                       @JsonProperty("metacritic") Metacritic metacritic,
                       @JsonProperty("release_date") ReleaseDate releaseDate) {

      public record Platforms(@JsonProperty("windows") boolean windows,
                              @JsonProperty("mac") boolean mac,
                              @JsonProperty("linux") boolean linux) {

      }

      public record Metacritic(@JsonProperty("score") int score,
                               @JsonProperty("url") String url) {

      }

      public record ReleaseDate(@JsonProperty("coming_soon") boolean comingSoon,
                                @JsonProperty("date") String date) {

      }

      @JsonIgnoreProperties(ignoreUnknown = true)
      public record PriceOverview(@JsonProperty("currency") String currency,
                                  @JsonProperty("final") Long price) {

      }
    }
  }
}

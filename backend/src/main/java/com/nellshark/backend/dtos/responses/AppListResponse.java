package com.nellshark.backend.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AppListResponse(@JsonProperty("applist") AppListRoot appListRoot) {

  public record AppListRoot(@JsonProperty("apps") List<App> apps) {

    public record App(@JsonProperty("appid") Long appId,
                      @JsonProperty("name") String name) {

    }
  }
}

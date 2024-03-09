package com.nellshark.backend.models.clientresponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/* {
    "applist": {
        "apps": [
            {
                "appid": 1941401,
                "name": ""
            },
            {
                "appid": 1897482,
                "name": ""
            },
            ...
           ]
    }
} */
public record AppList(@JsonProperty("applist") AppListRoot appListRoot) {

  public record AppListRoot(@JsonProperty("apps") List<App> apps) {

    public record App(@JsonProperty("appid") Long appId,
                      @JsonProperty("name") String name) {

    }
  }
}

package com.nellshark.backend.responses;

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
public record AppList(Applist applist) {
    public record Applist(List<App> apps) {
    }

    public record App(Long appid, String name) {
    }
}

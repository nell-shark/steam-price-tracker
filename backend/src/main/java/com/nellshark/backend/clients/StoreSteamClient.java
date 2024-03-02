package com.nellshark.backend.clients;

import com.nellshark.backend.responses.AppDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Component
public interface StoreSteamClient {
    @GetExchange("/api/appdetails")
    AppDetails getAppDetails(@RequestParam("appids") long gameId,
                             @RequestParam(value = "l", defaultValue = "english") String language);
}

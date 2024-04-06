package com.nellshark.backend.clients;

import com.nellshark.backend.models.responses.AppDetailsResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Component
public interface StoreSteamClient {

  String PRICE_OVERVIEW_FILTER = "price_overview";

  @GetExchange("/api/appdetails")
  AppDetailsResponse getAppDetails(
      @RequestParam("appids") long appId,
      @Nullable @RequestParam(value = "filter", required = false) String filter,
      @Nullable @RequestParam(value = "cc", required = false) String countryCode,
      @Nullable @RequestParam(value = "l", defaultValue = "english") String language
  );
}

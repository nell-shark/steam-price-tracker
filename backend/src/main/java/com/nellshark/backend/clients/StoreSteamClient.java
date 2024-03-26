package com.nellshark.backend.clients;

import com.nellshark.backend.models.clientresponses.AppDetails;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Component
public interface StoreSteamClient {

  String PRICE_OVERVIEW_FILTER = "price_overview";

  @GetExchange("/api/appdetails")
  AppDetails getAppDetails(
      @RequestParam("appids") long appId,
      @RequestParam(value = "filter", required = false) @Nullable String filter,
      @RequestParam(value = "cc", required = false) @Nullable String countryCode,
      @RequestParam(value = "l", defaultValue = "english") @Nullable String language);
}

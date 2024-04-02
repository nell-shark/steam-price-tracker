package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.enums.Currency;
import com.nellshark.backend.models.responses.AppDetailsResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreSteamService extends AbstractSteamService {

  private final StoreSteamClient storeSteamClient;

  @Nullable
  public AppDetailsResponse getAppDetails(
      long id,
      @Nullable String filter,
      @Nullable Currency currency) {
    log.info("Getting app info: id={}", id);

    handleRateLimit();

    String countryCode = Optional.ofNullable(currency)
        .map(Currency::getCountryCode)
        .orElse(null);

    AppDetailsResponse appDetailsResponse = storeSteamClient.getAppDetails(
        id, filter, countryCode, null);

    if (appDetailsResponse == null
        || appDetailsResponse.getApp() == null
        || !appDetailsResponse.getApp().success()
        || appDetailsResponse.getApp().data() == null) {
      return null;
    }

    return appDetailsResponse;
  }
}

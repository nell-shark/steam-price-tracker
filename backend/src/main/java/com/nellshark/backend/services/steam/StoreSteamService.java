package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.models.Currency;
import com.nellshark.backend.models.clientresponses.AppDetails;
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
  public AppDetails getAppDetails(
      long id,
      @Nullable String filter,
      @Nullable Currency currency) {
    log.info("Getting app info: id={}", id);

    handleRateLimit();

    String countryCode = Optional.ofNullable(currency)
        .map(Currency::getCountryCode)
        .orElse(null);

    AppDetails appDetails = storeSteamClient.getAppDetails(
        id, filter, countryCode, null);

    if (appDetails == null
        || appDetails.getApp() == null
        || !appDetails.getApp().success()
        || appDetails.getApp().data() == null) {
      return null;
    }

    return appDetails;
  }
}

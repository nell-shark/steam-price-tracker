package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.models.App;
import com.nellshark.backend.models.clientresponses.AppDetails;
import com.nellshark.backend.utils.MappingUtils;
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
  public App getAppInfo(long id) {
    log.info("Getting app info: id={}", id);

    handleRateLimit();

    AppDetails appDetails = storeSteamClient.getAppDetails(id, null);

    if (appDetails == null
        || appDetails.getApp() == null
        || !appDetails.getApp().success()
        || appDetails.getApp().data() == null) {
      return null;
    }

    return MappingUtils.toApp(appDetails.getApp().data());
  }
}

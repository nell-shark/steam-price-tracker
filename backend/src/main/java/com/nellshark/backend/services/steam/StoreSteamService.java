package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.models.App;
import com.nellshark.backend.models.clientresponses.AppDetails;
import com.nellshark.backend.models.clientresponses.AppDetails.App.Data.ReleaseDate;
import com.nellshark.backend.utils.MappingUtils;
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
  public App getAppInfo(long id) {
    log.info("Getting a app info: id={}", id);
    handleRateLimit();

    AppDetails appDetails = storeSteamClient.getAppDetails(id, null);

    AppDetails.App app = appDetails.getApp();

    if (!app.success() || app.data() == null) {
      return null;
    }

    AppDetails.App.Data data = app.data();
    boolean comingSoon = Optional.ofNullable(data.releaseDate())
        .map(ReleaseDate::comingSoon)
        .orElse(false);

    if (data.isFree() || comingSoon) {
      return null;
    }

    return MappingUtils.toApp(data);
  }
}

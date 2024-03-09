package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.GameType;
import com.nellshark.backend.models.clientresponses.AppDetails;
import com.nellshark.backend.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreSteamService extends AbstractSteamService {

  private final StoreSteamClient storeSteamClient;

  @Nullable
  public Game getGameInfo(long id) {
    log.info("Getting a game info: id={}", id);
    handleRateLimit();

    AppDetails appDetails = storeSteamClient.getAppDetails(id, null);
    AppDetails.App app = appDetails.getApp();

    if (!app.success()) {
      return null;
    }

    AppDetails.App.Data data = app.data();

    if (!EnumUtils.isValidEnumIgnoreCase(GameType.class, data.type())
        || data.isFree()) {
      return null;
    }

    return MappingUtils.toGame(data);
  }
}

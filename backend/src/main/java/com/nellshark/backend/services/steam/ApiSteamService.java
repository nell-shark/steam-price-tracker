package com.nellshark.backend.services.steam;

import com.nellshark.backend.clients.ApiSteamClient;
import com.nellshark.backend.models.responses.AppListReponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiSteamService extends AbstractSteamService {

  private final ApiSteamClient apiSteamClient;

  public List<Long> getAllSteamAppIds() {
    log.info("Getting all app ids from steam api");

    handleRateLimit();

    return apiSteamClient.getAppList()
        .appListRoot()
        .apps()
        .stream()
        .filter(Objects::nonNull)
        .filter(app -> StringUtils.isNotBlank(app.name()))
        .map(AppListReponse.AppListRoot.App::appId)
        .toList();
  }
}

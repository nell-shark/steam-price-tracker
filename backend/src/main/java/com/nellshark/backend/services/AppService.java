package com.nellshark.backend.services;

import static java.util.Objects.isNull;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.exceptions.AppNotFoundException;
import com.nellshark.backend.models.App;
import com.nellshark.backend.repositories.AppRepository;
import com.nellshark.backend.services.steam.ApiSteamService;
import com.nellshark.backend.services.steam.StoreSteamService;
import com.nellshark.backend.utils.MappingUtils;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppService {

  private final AppRepository appRepository;
  private final ApiSteamService apiSteamService;
  private final StoreSteamService storeSteamService;
  private final BlockedAppService blockedAppService;

  public List<AppDTO> getAllAppDTOs() {
    log.info("Getting all app DTOs");
    return appRepository.findAll()
        .stream()
        .map(MappingUtils::toAppDTO)
        .toList();
  }

  private List<App> getAllApps() {
    log.info("Getting all apps");
    return appRepository.findAll();
  }

  public App getAppById(long id) {
    log.info("Getting app by id: {}", id);
    return appRepository.findById(id)
        .orElseThrow(() -> new AppNotFoundException("App is not found id=" + id));
  }

  public List<AppDTO> getAppDTOsByPrefixName(@NonNull String prefixName) {
    log.info("Getting app DTOs by prefix name");
    prefixName = StringUtils.stripToNull(prefixName);

    List<App> apps = isNull(prefixName)
        ? getAllApps()
        : appRepository.findByNameStartsWithIgnoreCaseOrderByType(prefixName);

    return apps.stream()
        .map(MappingUtils::toAppDTO)
        .toList();
  }

  private List<Long> getAllAppIds() {
    log.info("Getting all app ids");
    return appRepository.findAllIds();
  }

  @Scheduled(cron = "@daily")
  @EventListener(ApplicationReadyEvent.class)
  public void checkForNewAppsPeriodically() {
    log.info("Check new apps");
    List<Long> allSteamAppIds = apiSteamService.getAllSteamAppIds();
    List<Long> appIdsFromDb = getAllAppIds();
    List<Long> blockedAppIds = blockedAppService.getBlockedAppIds();

    allSteamAppIds.stream()
        .filter(Objects::nonNull)
        .filter(id -> !appIdsFromDb.contains(id))
        .filter(id -> !blockedAppIds.contains(id))
        .forEach(this::addNewApp);
  }

  private void addNewApp(long id) {
    App app = storeSteamService.getAppInfo(id);

    if (isNull(app) || app.isFree()) {
      blockedAppService.addAppToBlockList(id);
      return;
    }

    if (app.getReleaseDate().comingSoon()) {
      return;
    }

    log.info("New app added to db: appId={}", id);
    appRepository.save(app);
  }
}

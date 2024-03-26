package com.nellshark.backend.services;

import static com.nellshark.backend.clients.StoreSteamClient.PRICE_OVERVIEW_FILTER;
import static java.util.Objects.isNull;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.exceptions.AppNotFoundException;
import com.nellshark.backend.models.App;
import com.nellshark.backend.models.Currency;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.models.clientresponses.AppDetails;
import com.nellshark.backend.models.clientresponses.AppDetails.App.Data.PriceOverview;
import com.nellshark.backend.repositories.AppRepository;
import com.nellshark.backend.services.steam.ApiSteamService;
import com.nellshark.backend.services.steam.StoreSteamService;
import com.nellshark.backend.utils.MappingUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
  private final PriceService priceService;
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

  @Scheduled(fixedDelay = 1, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
  public void updateAppPricePeriodically() {
    log.info("Update app price");
    getAllApps().forEach(app -> {
      Map<Currency, Long> map = Arrays.stream(Currency.values())
          .map(currency ->
              storeSteamService.getAppDetails(app.getId(), PRICE_OVERVIEW_FILTER, currency))
          .filter(Objects::nonNull)
          .filter(appDetails -> !appDetails.getApp().data().isFree())
          .filter(appDetails -> appDetails.getApp().data().priceOverview() != null)
          .map(appDetails -> appDetails.getApp().data().priceOverview())
          .collect(Collectors.toMap(PriceOverview::currency, PriceOverview::price));

      priceService.savePrice(new Price(map, app));
    });
  }

  private void addNewApp(long id) {
    AppDetails appDetails = storeSteamService.getAppDetails(id, null, null);

    if (appDetails == null || appDetails.getApp().data().isFree()) {
      blockedAppService.addAppToBlockList(id);
      return;
    }

    if (appDetails.getApp().data().releaseDate().comingSoon()) {
      return;
    }

    App app = MappingUtils.toApp(appDetails.getApp().data());
    saveApp(app);
  }

  private void saveApp(@NonNull App app) {
    log.info("Saving app: {}", app);
    appRepository.save(app);
  }
}

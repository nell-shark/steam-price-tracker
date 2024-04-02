package com.nellshark.backend.services;

import static com.nellshark.backend.clients.StoreSteamClient.PRICE_OVERVIEW_FILTER;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.enums.Currency;
import com.nellshark.backend.exceptions.AppNotFoundException;
import com.nellshark.backend.models.entities.App;
import com.nellshark.backend.models.entities.Price;
import com.nellshark.backend.models.responses.AppDetailsResponse;
import com.nellshark.backend.repositories.AppRepository;
import com.nellshark.backend.services.steam.ApiSteamService;
import com.nellshark.backend.services.steam.StoreSteamService;
import com.nellshark.backend.utils.CacheNames;
import com.nellshark.backend.utils.MappingUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppService {

  private static final int PAGE_SIZE = 10;

  private final AppRepository appRepository;
  private final ApiSteamService apiSteamService;
  private final PriceService priceService;
  private final StoreSteamService storeSteamService;
  private final BlockedAppService blockedAppService;

  private List<App> getAllApps() {
    log.info("Getting all apps");
    return appRepository.findAll();
  }

  public Page<AppDTO> getAppDTOsByPage(int page) {
    log.info("Getting app DTOs by page: page={}", page);
    page = page <= 0 ? 0 : --page;
    Pageable pageable = PageRequest.of(page, PAGE_SIZE);
    return appRepository.findAll(pageable)
        .map(MappingUtils::toAppDTO);
  }

  public Page<AppDTO> getAppDTOsByPrefixName(@NonNull String prefixName, int page) {
    log.info("Getting app DTOs by prefix name");
    prefixName = StringUtils.stripToNull(prefixName);

    if (prefixName == null) {
      return getAppDTOsByPage(1);
    }

    page = page <= 0 ? 0 : --page;
    Pageable pageable = PageRequest.of(page, PAGE_SIZE);

    return appRepository.findByNameStartsWithIgnoreCaseOrderByType(prefixName, pageable)
        .map(MappingUtils::toAppDTO);
  }

  @Cacheable(value = CacheNames.APPS, key = "#id")
  public App getAppById(long id) {
    log.info("Getting app by id: {}", id);
    return appRepository.findById(id)
        .orElseThrow(() -> new AppNotFoundException("App is not found id=" + id));
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
          .filter(details -> !details.getApp().data().isFree())
          .filter(details -> details.getApp().data().priceOverview() != null)
          .map(details -> details.getApp().data().priceOverview())
          .filter(p -> EnumUtils.isValidEnumIgnoreCase(Currency.class, p.currency()))
          .map(p -> Map.entry(EnumUtils.getEnumIgnoreCase(Currency.class, p.currency()), p.price()))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

      priceService.savePrice(new Price(map, app));
    });
  }

  private void addNewApp(long id) {
    AppDetailsResponse appDetailsResponse = storeSteamService.getAppDetails(id, null, null);

    if (appDetailsResponse == null) {
      blockedAppService.addAppToBlockList(id);
      return;
    }

    if (appDetailsResponse.getApp().data().isFree()
        || appDetailsResponse.getApp().data().releaseDate().comingSoon()) {
      return;
    }

    App app = MappingUtils.toApp(appDetailsResponse.getApp().data());
    saveApp(app);
  }

  private void saveApp(@NonNull App app) {
    log.info("Saving app: {}", app);
    appRepository.save(app);
  }

  @CacheEvict(value = CacheNames.APPS, key = "#id")
  public void deleteAppById(long id) {
    log.info("Deleting app");
    appRepository.deleteById(id);
  }
}

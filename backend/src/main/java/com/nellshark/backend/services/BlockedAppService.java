package com.nellshark.backend.services;

import com.nellshark.backend.models.entities.BlockedApp;
import com.nellshark.backend.repositories.BlockedAppRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlockedAppService {

  private final BlockedAppRepository blockedAppRepository;
  private final AppService appService;

  /**
   * Constructs a BlockedAppService with the specified BlockedAppRepository and AppService.
   *
   * @param blockedAppRepository The repository for managing blocked apps.
   * @param appService           The service for managing application-related operations. Note:
   *                             '@Lazy' annotation added to resolve circular dependency between
   *                             beans.
   */
  public BlockedAppService(
      BlockedAppRepository blockedAppRepository,
      @Lazy AppService appService) {
    this.blockedAppRepository = blockedAppRepository;
    this.appService = appService;
  }

  public List<Long> getBlockedAppIds() {
    log.info("Getting all blocked apps");
    return blockedAppRepository.findAll()
        .stream()
        .map(BlockedApp::getId)
        .toList();
  }

  public void addAppToBlockList(long appId) {
    log.info("Adding app to block list: appId={}", appId);
    BlockedApp blockedApp = new BlockedApp(appId);
    blockedAppRepository.save(blockedApp);
    appService.deleteAppById(appId);
  }
}

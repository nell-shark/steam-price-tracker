package com.nellshark.backend.services;

import com.nellshark.backend.models.BlockedApp;
import com.nellshark.backend.repositories.BlockedAppRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockedAppService {

  private final BlockedAppRepository blockedAppRepository;

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
  }
}

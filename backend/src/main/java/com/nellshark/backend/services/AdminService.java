package com.nellshark.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

  private final BlockedAppService blockedAppService;

  public void addAppToBlockList(long id) {
    log.info("Add app to block list: id={}", id);
    blockedAppService.addAppToBlockList(id);
  }
}

package com.nellshark.backend.services;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheManagerService {

  private final CacheManager cacheManager;

  @Async
  @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS)
  public void reportCurrentTime() {
    Optional.ofNullable(cacheManager.getCache("app")).ifPresent(Cache::clear);
  }
}

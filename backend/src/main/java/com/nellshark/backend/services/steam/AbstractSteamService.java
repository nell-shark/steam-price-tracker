package com.nellshark.backend.services.steam;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractSteamService {

  protected static final int STEAM_API_REQUEST_LIMIT = 50;
  protected static final AtomicInteger countOfSteamRequests = new AtomicInteger(0);

  protected void handleRateLimit() {
    if (countOfSteamRequests.incrementAndGet() <= STEAM_API_REQUEST_LIMIT) {
      return;
    }
    try {
      TimeUnit.MINUTES.sleep(5);
      countOfSteamRequests.set(0);
    } catch (InterruptedException e) {
      Thread.currentThread()
          .interrupt();
      throw new RuntimeException("Sleep interrupted while handling rate limit", e);
    }
  }
}

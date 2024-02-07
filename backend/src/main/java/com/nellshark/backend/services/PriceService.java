package com.nellshark.backend.services;

import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {
    private final PriceRepository priceRepository;

    public void savePrice(@NonNull Price price) {
        log.info("Save price to db: {}", price);
        priceRepository.save(price);
    }

    @Async
    @Scheduled(cron = "@daily")
    @Transactional
    public void deleteOldPricesPeriodically() {
        log.info("Deleting old prices");
        LocalDateTime deletingTime = LocalDateTime.now().minusYears(1);
        priceRepository.deleteOlderThan(deletingTime);
    }
}

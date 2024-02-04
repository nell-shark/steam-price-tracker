package com.nellshark.backend.services;

import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {
    private final PriceRepository priceRepository;

    public void savePrice(@NonNull Price price) {
        log.info("Save price to db: {}", price);
        priceRepository.save(price);
    }
}

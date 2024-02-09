package com.nellshark.backend.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD("US"),
    EUR("DE"),
    RUB("RU"),
    KZT("KZ");

    private final String countryCode;
}

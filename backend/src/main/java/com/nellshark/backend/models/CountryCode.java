package com.nellshark.backend.models;

import lombok.Getter;

@Getter
public enum CountryCode {
    US("USD"),
    DE("EUR"),
    RU("RUB"),
    KZ("KZT");

    private final String currency;

    CountryCode(String currency) {
        this.currency = currency;
    }
}

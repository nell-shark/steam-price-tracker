package com.nellshark.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "prices")
@Entity
@Data
@NoArgsConstructor
public class Price {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_usd", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long priceUsd;

    @Column(name = "price_eur", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long priceEur;

    @Column(name = "price_rub", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long priceRub;

    @Column(name = "price_kzt", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long priceKzt;

    @Column(name = "local_date", nullable = false, updatable = false, columnDefinition = "DATE")
    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false, updatable = false)
    @JsonIgnore
    private Game game;

    public Price(Long priceUsd,
                 Long priceEur,
                 Long priceRub,
                 Long priceKzt,
                 LocalDate localDate,
                 Game game) {
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.priceRub = priceRub;
        this.priceKzt = priceKzt;
        this.localDate = localDate;
        this.game = game;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", priceUsd=" + priceUsd +
                ", priceEur=" + priceEur +
                ", priceRub=" + priceRub +
                ", priceKzt=" + priceKzt +
                ", localDate=" + localDate +
                ", gameId=" + game.getId() +
                '}';
    }
}

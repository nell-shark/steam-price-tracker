package com.nellshark.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Table(name = "prices")
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Price {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USD", nullable = false, updatable = false)
    private Long usd;

    @Column(name = "EUR", nullable = false, updatable = false)
    private Long eur;

    @Column(name = "RUB", nullable = false, updatable = false)
    private Long rub;

    @Column(name = "KZT", nullable = false, updatable = false)
    private Long kzt;

    @Column(name = "local_date", nullable = false, updatable = false)
    @JsonFormat(shape = STRING)
    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false, updatable = false)
    @JsonIgnore
    private Game game;

    public Price(Long usd,
                 Long eur,
                 Long rub,
                 Long kzt,
                 LocalDate localDate,
                 Game game) {
        this.usd = usd;
        this.eur = eur;
        this.rub = rub;
        this.kzt = kzt;
        this.localDate = localDate;
        this.game = game;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", usd=" + usd +
                ", eur=" + eur +
                ", rub=" + rub +
                ", kzt=" + kzt +
                ", localDate=" + localDate +
                ", gameId=" + game.getId() +
                '}';
    }
}

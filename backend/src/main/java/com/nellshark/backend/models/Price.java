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
import lombok.ToString;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Table(name = "prices")
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "game"})
public class Price {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usd", nullable = false, updatable = false)
    private long usd;

    @Column(name = "eur", nullable = false, updatable = false)
    private long eur;

    @Column(name = "rub", nullable = false, updatable = false)
    private long rub;

    @Column(name = "kzt", nullable = false, updatable = false)
    private long kzt;

    @Column(name = "local_date", nullable = false, updatable = false)
    @JsonFormat(shape = STRING)
    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false, updatable = false)
    @JsonIgnore
    private Game game;

    public Price(long usd,
                 long eur,
                 long rub,
                 long kzt,
                 LocalDate localDate,
                 Game game) {
        this.usd = usd;
        this.eur = eur;
        this.rub = rub;
        this.kzt = kzt;
        this.localDate = localDate;
        this.game = game;
    }
}

package com.nellshark.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Table(name = "games")
@Entity
@Data
@NoArgsConstructor
public class Game {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Price> prices;

    public Game(Long id) {
        this.id = id;
    }
}

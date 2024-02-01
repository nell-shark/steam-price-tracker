package com.nellshark.backend.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Table(name = "games")
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name"})
public class Game {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "windows", nullable = false)
    private boolean windows;

    @Column(name = "mac", nullable = false)
    private boolean mac;

    @Column(name = "linux", nullable = false)
    private boolean linux;

    @OneToMany(mappedBy = "game",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    private List<Price> prices;

    public Game(long id,
                String name,
                String description,
                String image,
                boolean windows,
                boolean mac,
                boolean linux) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.windows = windows;
        this.linux = linux;
        this.mac = mac;
    }
}

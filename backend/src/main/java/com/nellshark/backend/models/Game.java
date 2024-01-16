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
public class Game {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "windows", nullable = false)
    private Boolean windows;

    @Column(name = "mac", nullable = false)
    private Boolean mac;

    @Column(name = "linux", nullable = false)
    private Boolean linux;

    @OneToMany(mappedBy = "game",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @ToString.Exclude
    private List<Price> prices;

    public Game(Long id,
                String name,
                String description,
                String imageUrl,
                Boolean windows,
                Boolean linux,
                Boolean mac) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.windows = windows;
        this.linux = linux;
        this.mac = mac;
    }
}

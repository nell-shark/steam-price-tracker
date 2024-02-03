package com.nellshark.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metacritic {
    @Column(name = "metacritic_score")
    private int score;

    @Column(name = "metacritic_url")
    private String url;
}

package com.nellshark.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"score", "url"})
@ToString(of = {"score", "url"})
public class Metacritic {

  @Column(name = "metacritic_score")
  private Integer score;

  @Column(name = "metacritic_url")
  private String url;
}

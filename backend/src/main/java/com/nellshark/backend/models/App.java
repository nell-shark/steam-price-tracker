package com.nellshark.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nellshark.backend.enums.Platform;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "apps", indexes = @Index(name = "name_index", columnList = "name"))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name"})
@JsonInclude(Include.NON_NULL)
public class App implements Serializable {

  @Id
  private long id;

  @Column(name = "name", nullable = false)
  @lombok.NonNull
  private String name;

  @Column(name = "type", nullable = false)
  @lombok.NonNull
  private String type;

  @Column(name = "header_image", nullable = false, columnDefinition = "TEXT")
  @lombok.NonNull
  private String headerImage;

  @Column(name = "is_free", nullable = false)
  private boolean isFree;

  @CollectionTable(name = "platforms")
  @Column(name = "platform")
  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = Platform.class, fetch = FetchType.LAZY)
  private List<Platform> platforms;

  @Column(name = "short_description", columnDefinition = "TEXT")
  private String shortDescription;

  @Column(name = "developers", columnDefinition = "TEXT")
  private String developers;

  @Column(name = "publishers", columnDefinition = "TEXT")
  private String publishers;

  @Column(name = "website")
  private String website;

  @Embedded
  private Metacritic metacritic;

  @Embedded
  private ReleaseDate releaseDate;

  @OneToMany(mappedBy = "app",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.REMOVE)
  @Builder.Default
  private List<Price> prices = new ArrayList<>();

  @ManyToMany(mappedBy = "favoriteApps", fetch = FetchType.LAZY)
  @Builder.Default
  @JsonIgnore
  private List<User> likedUsers = new ArrayList<>();

  @Embeddable
  public record Metacritic(
      @Column(name = "metacritic_score") int score,
      @Column(name = "metacritic_url") String url)

      implements Serializable {

  }

  @Embeddable
  public record ReleaseDate(
      @Column(name = "coming_soon")
      boolean comingSoon,
      @Column(name = "release_date")
      @JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
      LocalDate releaseDate)

      implements Serializable {

  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    type = type.toUpperCase();
    prices = Optional.ofNullable(prices).orElse(List.of());
    likedUsers = Optional.ofNullable(likedUsers).orElse(List.of());
  }
}


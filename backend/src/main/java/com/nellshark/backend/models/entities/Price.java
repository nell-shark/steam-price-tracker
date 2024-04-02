package com.nellshark.backend.models.entities;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nellshark.backend.enums.Currency;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "prices")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "app"})
public class Price implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "currency_prices",
      joinColumns = @JoinColumn(name = "price_id")
  )
  @MapKeyEnumerated(EnumType.STRING)
  @MapKeyColumn(name = "currency")
  @Column(name = "price")
  private Map<Currency, Long> currencyPriceMap;

  @ManyToOne
  @JoinColumn(name = "app_id", nullable = false, updatable = false)
  @JsonIgnore
  private App app;

  @CreationTimestamp
  @Column(name = "created_time", nullable = false, updatable = false)
  @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime createdTime;

  public Price(@NonNull Map<Currency, Long> currencyPriceMap, @NonNull App app) {
    this.currencyPriceMap = currencyPriceMap;
    this.app = app;
  }
}

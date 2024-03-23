package com.nellshark.backend.utils;

import static java.util.Locale.ENGLISH;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.models.App;
import com.nellshark.backend.models.Platform;
import com.nellshark.backend.models.clientresponses.AppDetails;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Slf4j
public final class MappingUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER;

  static {
    DateTimeFormatter pattern1 = DateTimeFormatter.ofPattern("MMM d, yyyy", ENGLISH);
    DateTimeFormatter pattern2 = DateTimeFormatter.ofPattern("d MMM, yyyy", ENGLISH);

    DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendOptional(pattern1)
        .appendOptional(pattern2)
        .toFormatter();
  }

  private MappingUtils() {
    throw new AssertionError();
  }

  public static AppDTO toAppDTO(@NonNull App app) {
    return new AppDTO(app.getId(), app.getName(), app.getHeaderImage());
  }

  public static App toApp(@NonNull AppDetails.App.Data data) {
    List<Platform> platforms = parsePlatforms(data.platforms());

    LocalDate releaseDate = parseReleaseDate(data.releaseDate());

    String developers = collectNonEmptyValues(data.developers());

    String publishers = collectNonEmptyValues(data.publishers());

    App.Metacritic metacritic = getMetacritic(data.metacritic());

    return App.builder()
        .id(data.steamAppId())
        .name(data.name())
        .appType(data.type())
        .headerImage(data.headerImage())
        .platforms(platforms)
        .shortDescription(data.shortDescription())
        .releaseDate(releaseDate)
        .developers(developers)
        .publishers(publishers)
        .website(data.website())
        .metacritic(metacritic)
        .build();
  }

  @Nullable
  private static List<Platform> parsePlatforms(@Nullable AppDetails.App.Data.Platforms platforms) {
    if (platforms == null) {
      return null;
    }

    return Stream.of(
            platforms.windows() ? Platform.WINDOWS : null,
            platforms.mac() ? Platform.MAC : null,
            platforms.linux() ? Platform.LINUX : null
        )
        .filter(Objects::nonNull)
        .toList();
  }

  @Nullable
  private static LocalDate parseReleaseDate(@Nullable AppDetails.App.Data.ReleaseDate releaseDate) {
    if (releaseDate == null) {
      return null;
    }

    try {
      return LocalDate.parse(releaseDate.date(), DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      log.warn("Failed to parse date '{}': {}", e.getParsedString(), e.getMessage());
      return null;
    }
  }

  @Nullable
  private static String collectNonEmptyValues(@Nullable List<String> values) {
    if (values == null) {
      return null;
    }

    return values.stream()
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(", "));
  }

  @Nullable
  private static App.Metacritic getMetacritic(AppDetails.App.Data.Metacritic metacritic) {
    return Optional.ofNullable(metacritic)
        .map(m -> new App.Metacritic(m.score(), m.url()))
        .orElse(null);
  }
}

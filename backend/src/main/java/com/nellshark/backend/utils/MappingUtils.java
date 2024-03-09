package com.nellshark.backend.utils;

import static java.util.Locale.ENGLISH;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.GameType;
import com.nellshark.backend.models.Metacritic;
import com.nellshark.backend.models.Platform;
import com.nellshark.backend.models.clientresponses.AppDetails;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

  public static GameDTO toGameDTO(@NonNull Game game) {
    return new GameDTO(game.getId(), game.getName(), game.getHeaderImage());
  }


  public static Game toGame(@NonNull AppDetails.App.Data data) {
    GameType gameType = GameType.valueOf(data.type().toUpperCase());

    String developers = collectNonEmptyValues(data.developers());
    String publishers = collectNonEmptyValues(data.publishers());

    Metacritic metacritic = Optional.ofNullable(data.metacritic())
        .map(m -> new Metacritic(m.score(), m.url()))
        .orElse(null);

    LocalDate releaseDate = parseReleaseDate(data.releaseDate());

    List<Platform> platforms = getPlatforms(data.platforms());

    return new Game(data.steamAppId(),
        data.name(),
        gameType,
        data.headerImage(),
        platforms,
        data.shortDescription(),
        releaseDate,
        developers,
        publishers,
        data.website(),
        metacritic
    );
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
  private static LocalDate parseReleaseDate(@Nullable AppDetails.App.Data.ReleaseDate releaseDate) {
    if (releaseDate != null && !releaseDate.comingSoon()) {
      try {
        return LocalDate.parse(releaseDate.date(), DATE_TIME_FORMATTER);
      } catch (DateTimeParseException e) {
        log.warn("Failed to parse date '{}': {}", e.getParsedString(), e.getMessage());
      }
    }
    return null;
  }

  @Nullable
  private static List<Platform> getPlatforms(@Nullable AppDetails.App.Data.Platforms platforms) {
    if (platforms == null) {
      return null;
    }

    List<Platform> platformList = new ArrayList<>();

    if (platforms.windows()) {
      platformList.add(Platform.WINDOWS);
    }
    if (platforms.mac()) {
      platformList.add(Platform.MAC);
    }
    if (platforms.linux()) {
      platformList.add(Platform.LINUX);
    }
    return platformList;
  }
}

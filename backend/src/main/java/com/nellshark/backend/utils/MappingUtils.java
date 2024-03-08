package com.nellshark.backend.utils;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.GameType;
import com.nellshark.backend.models.Metacritic;
import com.nellshark.backend.models.clientresponses.AppDetails;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

public final class MappingUtils {
    private MappingUtils() {
        throw new AssertionError();
    }

    public static GameDTO toGameDTO(Game game) {
        return new GameDTO(game.getId(), game.getName(), game.getHeaderImage());
    }

    public static Game toGame(AppDetails.App.Data data) {
        GameType gameType = GameType.valueOf(data.type()
                                                     .toUpperCase());
        String developers = data.developers() != null
                ? data.developers()
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "))
                : null;

        String publishers = data.publishers() != null
                ? data.publishers()
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "))
                : null;

        Metacritic metacritic = data.metacritic() != null
                ? new Metacritic(data.metacritic()
                                         .score(),
                                 data.metacritic()
                                         .url())
                : null;

        return new Game(data.steamAppId(),
                        data.name(),
                        gameType,
                        data.headerImage(),
                        null,
                        data.shortDescription(),
                        null,
                        developers,
                        publishers,
                        data.website(),
                        metacritic);
    }
}

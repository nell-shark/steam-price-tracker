package com.nellshark.backend.services;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.exceptions.SteamApiException;
import com.nellshark.backend.models.Currency;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import com.nellshark.backend.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final PriceService priceService;
    private final SteamService steamService;
    private final ApiSteamService apiSteamService;
    private final StoreSteamService storeSteamService;
    private final BlockedGameService blockedGameService;

    public List<GameDTO> getAllGameDTOs() {
        log.info("Getting all game DTOs");
        return gameRepository.findAll()
                .stream()
                .map(MappingUtils::toDTO)
                .toList();
    }

    private List<Game> getAllGames() {
        log.info("Getting all games");
        return gameRepository.findAll();
    }

    public Game getGameById(long id) {
        log.info("Getting game by id: {}", id);
        return gameRepository
                .findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game is not found id=" + id));
    }

    public List<GameDTO> getGameDTOsByPrefixName(@NonNull String prefixName) {
        log.info("Getting game DTOs by prefix name");
        prefixName = StringUtils.stripToNull(prefixName);

        List<Game> games = isNull(prefixName)
                ? getAllGames()
                : gameRepository.findByNameStartsWithIgnoreCase(prefixName);

        return games.stream()
                .map(MappingUtils::toDTO)
                .toList();
    }

    private Set<Long> getAllGameIds() {
        log.info("Getting all game ids");
        return gameRepository.findAllIds();
    }

    @Scheduled(cron = "@daily")
    @EventListener(ApplicationReadyEvent.class)
    public void checkForNewGamesPeriodically() {
        log.info("Check new games");
        Set<Long> allSteamGameIds = apiSteamService.getAllSteamGameIds();
        Set<Long> gameIdsFromDb = getAllGameIds();
        Set<Long> blockedGameIds = blockedGameService.getBlockedGameIds();

        allSteamGameIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> !gameIdsFromDb.contains(id))
                .filter(id -> !blockedGameIds.contains(id))
                .forEach(this::addNewGame);
    }

    private void addNewGame(long id) {
        Game game = storeSteamService.getGameInfo(id);

        if (isNull(game)) {
            return;
        }

        log.info("New game added to db: gameId={}", id);
        gameRepository.save(game);
    }

    @Scheduled(fixedDelay = 1, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void updateGamePricesPeriodically() {
        log.info("Check new prices");
        getAllGames().stream()
                .map(this::getUpdatedGamePrice)
                .forEach(priceService::savePrice);
    }

    private Price getUpdatedGamePrice(@NonNull Game game) {
        log.info("Updating game price: game={}", game);

        Map<Currency, String> priceMap = Stream.of(Currency.values())
                .collect(HashMap::new,
                        (map, currency) -> map.put(currency, getPrice(game, currency)),
                        HashMap::putAll
                );

        return new Price(game, priceMap);
    }

    @Nullable
    private String getPrice(@NonNull Game game, @NonNull Currency currency) {
        try {
            return steamService.getNewGamePrice(game.getId(), currency);
        } catch (SteamApiException e) {
            log.warn("Failed to get price for game {} in country {}. Reason: {}",
                    game.getId(), currency, e.getMessage()
            );
            return null;
        }
    }
}

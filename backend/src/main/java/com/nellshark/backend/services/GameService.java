package com.nellshark.backend.services;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.exceptions.SteamApiException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import com.nellshark.backend.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.nellshark.backend.models.CountryCode.DE;
import static com.nellshark.backend.models.CountryCode.KZ;
import static com.nellshark.backend.models.CountryCode.RU;
import static com.nellshark.backend.models.CountryCode.US;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final PriceService priceService;
    private final SteamService steamService;
    private final GameBlockedService gameBlockedService;

    public List<GameDTO> getAllGameDTOs() {
        log.info("Getting all games DTO");
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
                .orElseThrow(() -> new GameNotFoundException("Game not found id=" + id));
    }

    private List<Long> getAllGameIds() {
        log.info("Getting all game ids");
        return gameRepository.findAllIds();
    }

    @Scheduled(cron = "@daily")
    public void checkForNewGamesPeriodically() {
        log.info("Check new games");
        List<Long> gameIdsFromDb = getAllGameIds();

        List<Long> allSteamGameIds = steamService.getAllSteamGameIds();
        List<Long> blockedGameIds = gameBlockedService.getGameBlockedIds();

        allSteamGameIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> !gameIdsFromDb.contains(id))
                .filter(id -> !blockedGameIds.contains(id))
                .forEach(this::addNewGame);
    }

    private void addNewGame(long id) {
        Game game = steamService.getGameInfo(id);

        if (isNull(game)) {
            return;
        }

        log.info("New game added to db: gameId={}", id);
        gameRepository.save(game);
    }

    @Scheduled(initialDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void updateGamePricesPeriodically() {
        log.info("Check new prices");
        getAllGames().stream()
                .map(this::getUpdatedGamePrice)
                .forEach(priceService::savePrice);
    }

    private Price getUpdatedGamePrice(@NonNull Game game) {
        log.info("Updating game price: game={}", game);

        Map<CountryCode, Long> priceMap = Stream.of(CountryCode.values())
                .collect(HashMap::new,
                        (map, countryCode) -> map.put(countryCode, getPrice(game, countryCode)),
                        HashMap::putAll
                );

        return new Price(
                priceMap.get(US),
                priceMap.get(DE),
                priceMap.get(RU),
                priceMap.get(KZ),
                LocalDateTime.now(),
                game
        );
    }

    @Nullable
    private Long getPrice(Game game, CountryCode countryCode) {
        try {
            return steamService.getNewGamePrice(game.getId(), countryCode);
        } catch (SteamApiException e) {
            log.warn("Failed to get price for game {} in country {}. Reason: {}",
                    game.getId(), countryCode, e.getMessage()
            );
            return null;
        }
    }
}

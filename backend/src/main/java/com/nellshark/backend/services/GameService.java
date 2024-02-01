package com.nellshark.backend.services;

import com.nellshark.backend.dto.GameDTO;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import com.nellshark.backend.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nellshark.backend.models.CountryCode.DE;
import static com.nellshark.backend.models.CountryCode.KZ;
import static com.nellshark.backend.models.CountryCode.RU;
import static com.nellshark.backend.models.CountryCode.US;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private static final String STEAM_GAMES_CHECK_CRON_EXPRESSION = "@daily";
    private static final String PRICE_UPDATE_CRON_EXPRESSION = "0 * * * * *";

    private final GameRepository gameRepository;
    private final PriceService priceService;
    private final SteamService steamService;

    public List<GameDTO> getAllGamesDTO() {
        log.info("Getting all games DTO");
        return gameRepository.findAll()
                .stream()
                .map(MappingUtils::toDTO)
                .toList();
    }

    private List<Game> getAllGames() {
        log.info("Getting all games");
        return gameRepository.findAll()
                .stream()
                .toList();
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

    @Scheduled(cron = STEAM_GAMES_CHECK_CRON_EXPRESSION)
    public void scheduleCheckingNewGames() {
        log.info("Check new games");
        List<Long> allSteamGamesId = steamService.getAllSteamGameIds();
        List<Long> gameIdsFromDb = getAllGameIds();

        long newGamesCount = allSteamGamesId.stream()
                .filter(id -> !gameIdsFromDb.contains(id))
                .peek(this::addNewGame)
                .count();

        log.info("{} new games found", newGamesCount);
    }

    private void addNewGame(long id) {
        log.info("Adding a new game: id={}", id);
        Game game = steamService.getGameInfo(id);
        if (nonNull(game)) {
            log.info("Saving {}", game);
            gameRepository.save(game);
        }
    }

    //    @Scheduled(cron = PRICE_UPDATE_CRON_EXPRESSION)
    public void scheduleUpdatingPrices() {
        log.info("Check new prices");

        getAllGames().stream()
                .map(this::getNewGamePrice)
                .forEach(priceService::savePrice);
    }

    private Price getNewGamePrice(@NonNull Game game) {
        log.info("Updating game price: game={}", game);

        Map<CountryCode, Long> map = new HashMap<>();
        for (CountryCode countryCode : CountryCode.values()) {
            long price = steamService.getNewGamePrice(game.getId(), countryCode);
            map.put(countryCode, price);
            log.info("Price of gameId={}: {} {}", game.getId(), price, countryCode.getCurrency());
        }

        return new Price(
                map.get(US),
                map.get(DE),
                map.get(RU),
                map.get(KZ),
                LocalDate.now(),
                game
        );
    }
}

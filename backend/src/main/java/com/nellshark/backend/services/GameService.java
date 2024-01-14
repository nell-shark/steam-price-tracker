package com.nellshark.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.exceptions.UnexpectedJsonStructureException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nellshark.backend.models.CountryCode.DE;
import static com.nellshark.backend.models.CountryCode.KZ;
import static com.nellshark.backend.models.CountryCode.RU;
import static com.nellshark.backend.models.CountryCode.US;
import static com.nellshark.backend.models.CountryCode.values;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private static final String PRICE_UPDATE_CRON_EXPRESSION = "0 * * * * *";

    private final GameRepository gameRepository;
    private final PriceService priceService;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public List<Game> getAllGames() {
        log.info("Getting all games");
        return gameRepository.findAll();
    }

    public List<Long> getAllGameIds() {
        log.info("Getting all game ids");
        return gameRepository.getAllGameIds();
    }

    public Game getGameById(Long id) {
        log.info("Getting game by id: {}", id);
        return gameRepository
                .findByIdWithPrices(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found id=" + id));
    }

    @Scheduled(cron = PRICE_UPDATE_CRON_EXPRESSION)
    public void schedulePriceUpdate() {
        log.info("Staring getting a new price of the games");
        getAllGames().parallelStream()
                .map(this::getNewGamePrice)
                .forEach(priceService::savePrice);
    }

    private Price getNewGamePrice(Game game) {
        log.info("Updating game price: gameId={}", game.getId());
        Map<CountryCode, Long> map = new HashMap<>();

        for (CountryCode countryCode : values()) {
            String url = buildSteamApiPriceOverviewUrl(game.getId(), countryCode);
            String apiData = fetchSteamApiData(url);
            long finalPrice = getPriceFromSteamJsonResponse(apiData);
            map.put(countryCode, finalPrice);
            log.info("Price of game id={}: {} {}", game.getId(), finalPrice, countryCode.getCurrency());
        }

        return new Price(map.get(US), map.get(DE), map.get(RU), map.get(KZ), LocalDate.now(), game);
    }

    private String buildSteamApiPriceOverviewUrl(Long gameId, CountryCode countryCode) {
        return String.format(
                "https://store.steampowered.com/api/appdetails" +
                        "?appids=%d" +
                        "&filters=price_overview" +
                        "&cc=%s",
                gameId, countryCode
        );
    }

    private String fetchSteamApiData(String url) {
        log.info("Fetching steam api data: {}", url);
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (isNull(responseBody)) {
                throw new RuntimeException("Empty response for URL: " + url);
            }
            return responseBody.string();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching data for URL " + url, e);
        }
    }

    private Long getPriceFromSteamJsonResponse(String apiData) {
        try {
            JsonNode jsonNode = objectMapper.readTree(apiData);

            if (jsonNode.isEmpty()) {
                throw new UnexpectedJsonStructureException("Empty JSON parsing");
            }

            if (!jsonNode.elements().next().get("success").asBoolean()) {
                throw new GameNotFoundException("Game doesn't exist");
            }

            return jsonNode.elements().next()
                    .get("data")
                    .get("price_overview")
                    .get("final")
                    .asLong();
        } catch (JsonProcessingException e) {
            throw new UnexpectedJsonStructureException("Error parsing JSON response. Raw JSON data: " + apiData, e);
        }
    }

    public void addNewGame(Long steamId) {
        log.info("Adding a new game: id={}", steamId);
        Game game = new Game(steamId);
        Price price = getNewGamePrice(game);
        gameRepository.save(game);
        priceService.savePrice(price);
    }
}

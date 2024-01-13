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
import java.util.Optional;

import static com.nellshark.backend.models.CountryCode.DE;
import static com.nellshark.backend.models.CountryCode.KZ;
import static com.nellshark.backend.models.CountryCode.RU;
import static com.nellshark.backend.models.CountryCode.US;
import static com.nellshark.backend.models.CountryCode.values;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
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
        return gameRepository.findAll()
                .stream()
                .map(Game::getId)
                .toList();
    }

    public Long getGameById(Long id) {
        log.info("Getting game by id: {}", id);
        return gameRepository
                .findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found id=" + id))
                .getId();
    }

    @Scheduled(cron = "0 * * * * *")
    public void schedulePriceUpdate() {
        log.info("Staring getting a new price of the games");
        final String STEAM_PRICE_OVERVIEW_URL_TEMPLATE = "https://store.steampowered.com/api/appdetails" +
                "?filters=price_overview" +
                "&appids=%s" +
                "&cc=%s";

        getAllGames().parallelStream().forEach(game -> {
            try {
                Map<CountryCode, Long> map = new HashMap<>();
                for (CountryCode countryCode : values()) {
                    String url = String.format(STEAM_PRICE_OVERVIEW_URL_TEMPLATE, game.getId(), countryCode);
                    String apiData = fetchSteamApiData(url);
                    long price = getPriceFromSteamJsonResponse(apiData);
                    map.put(countryCode, price);
                    log.info("Price of game ID {}: {} {}", game.getId(), price, countryCode.getCurrency());
                }

                Price price = new Price(map.get(US), map.get(DE), map.get(RU), map.get(KZ), LocalDate.now(), game);
                priceService.savePrice(price);
            } catch (IOException e) {
                log.error("Error fetching data for game ID {}: {}", game, e.getMessage());
            }
        });
    }

    private String fetchSteamApiData(String url) throws IOException {
        log.info("Fetching steam api data: {}", url);
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = Optional.ofNullable(response.body())
                    .orElseThrow(() -> new IOException("Empty response for URL: " + url));

            return responseBody.string();
        }
    }

    private Long getPriceFromSteamJsonResponse(String apiData) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(apiData);

        if (jsonNode.isEmpty()) {
            throw new UnexpectedJsonStructureException("Empty JSON parsing: " + apiData);
        }

        JsonNode gameNode = jsonNode.elements().next();
        JsonNode dataNode = gameNode
                .path("data")
                .path("price_overview")
                .path("final");

        if (dataNode.isMissingNode() || dataNode.isNull() || !dataNode.isNumber()) {
            throw new UnexpectedJsonStructureException("Invalid JSON structure: " + apiData);
        }

        return dataNode.asLong();
    }
}

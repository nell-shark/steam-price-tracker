package com.nellshark.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.models.Game;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public List<Long> getAllGameIds() {
        log.info("Getting all game ids");
        return gameRepository.findAll()
                .stream()
                .map(Game::getId)
                .toList();
    }

    @Scheduled(cron = "0 * * * * *")
    public void schedulePriceUpdate() {
        final String STEAM_PRICE_URL = "https://store.steampowered.com/api/appdetails?filters=price_overview&appids=";

        getAllGameIds().forEach(gameId -> {
            try {
                String body = fetchSteamApiData(STEAM_PRICE_URL + gameId);
                log.info("Received response for game ID {}: {}", gameId, body);

                JsonNode jsonNode = objectMapper.readTree(body);
                long price = jsonNode.get(String.valueOf(gameId))
                        .get("data")
                        .get("price_overview")
                        .get("final")
                        .asLong();

                log.info("Price of game ID {}: {}", gameId, price);
            } catch (IOException e) {
                log.error("Error fetching data for game ID {}: {}", gameId, e.getMessage());
            }
        });
    }

    private String fetchSteamApiData(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = Optional.ofNullable(response.body())
                    .orElseThrow(() -> new IOException("Empty response for URL: " + url));
            return responseBody.string();
        }
    }
}

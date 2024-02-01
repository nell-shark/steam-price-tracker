package com.nellshark.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.exceptions.UnexpectedJsonStructureException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamService {
    private static final short STEAM_API_REQUEST_LIMIT = 150;
    private static final int FIVE_MINUTES_IN_MILLIS = 1000 * 60 * 5;
    private static short countOfSteamRequests = 0;

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public List<Long> getAllSteamGameIds() {
        log.info("Getting all steam games id");
        URL url = buildSteamAppListUrl();
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGameIds(jsonNode);
    }

    private URL buildSteamAppListUrl() {
        try {
            URIBuilder uri = new URIBuilder("https://api.steampowered.com/ISteamApps/GetAppList/v2");
            return uri.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode fetchSteamApiData(@NonNull URL url) {
        log.info("Fetching steam api data: {}", url);
        handleRateLimit();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody responseBody = response.body();

            Objects.requireNonNull(responseBody);

            String apiData = responseBody.string();

            return getJsonNode(apiData);
        } catch (IOException e) {
            throw new RuntimeException("Error fetching data from URL " + url, e);
        }
    }

    private void handleRateLimit() {
        log.debug("countOfSteamRequests = {}", countOfSteamRequests);

        if (++countOfSteamRequests != STEAM_API_REQUEST_LIMIT) {
            return;
        }

        try {
            Thread.sleep(FIVE_MINUTES_IN_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
        countOfSteamRequests = 0;
    }


    private JsonNode getJsonNode(@NonNull String apiData) {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(apiData);
        } catch (JsonProcessingException e) {
            throw new UnexpectedJsonStructureException("Error parsing JSON response. Raw JSON data: " + apiData, e);
        }

        if (jsonNode.isEmpty()) {
            throw new UnexpectedJsonStructureException("Empty JSON parsing");
        }

        return jsonNode;
    }

    private List<Long> parseGameIds(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode array = jsonNode
                .get("applist")
                .get("apps");

        if (!array.isArray()) {
            throw new UnexpectedJsonStructureException("Error parsing JSON response. JSON must be an array");
        }

        List<Long> gameIds = new ArrayList<>();

        for (JsonNode next : array) {
            if (!next.isObject()) {
                throw new UnexpectedJsonStructureException("Error getting data from new game");
            }

            String name = next.get("name").asText();

            if (StringUtils.isBlank(name)) {
                continue;
            }

            long appId = next.get("appid").asLong();
            gameIds.add(appId);
        }

        return gameIds;
    }

    @Nullable
    public Game getGameInfo(long gameId) {
        URL url = buildSteamAppDetailsUrl(gameId);
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGameInfo(jsonNode);
    }

    private URL buildSteamAppDetailsUrl(long gameId) {
        try {
            URIBuilder uri = new URIBuilder("https://store.steampowered.com/api/appdetails");
            uri.addParameter("appids", String.valueOf(gameId));
            return uri.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private Game parseGameInfo(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        if (!jsonNode.elements().next().get("success").asBoolean()) {
            return null;
        }

        long id = Long.parseLong(jsonNode.fieldNames().next());

        JsonNode data = jsonNode.elements().next().get("data");
        String name = data.get("name").asText();
        String description = data.get("short_description").asText();
        String image = data.get("header_image").asText();

        JsonNode platforms = data.get("platforms");
        boolean windows = platforms.get("windows").asBoolean();
        boolean mac = platforms.get("mac").asBoolean();
        boolean linux = platforms.get("linux").asBoolean();

        return new Game(
                id,
                name,
                description,
                image,
                windows,
                mac,
                linux
        );
    }

    public long getNewGamePrice(long gameId, CountryCode countryCode) {
        URL url = buildSteamPriceOverviewUrl(gameId, countryCode);
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGamePrice(jsonNode);
    }

    private URL buildSteamPriceOverviewUrl(long gameId, CountryCode countryCode) {
        try {
            URIBuilder uri = new URIBuilder("https://store.steampowered.com/api/appdetails");
            uri.addParameter("appids", String.valueOf(gameId));
            uri.addParameter("cc", countryCode.toString());
            uri.addParameter("filters", "price_overview");
            return uri.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private long parseGamePrice(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        if (!jsonNode.elements().next().get("success").asBoolean()) {
            throw new GameNotFoundException("Game doesn't exist");
        }

        return jsonNode.elements().next()
                .get("data")
                .get("price_overview")
                .get("final")
                .asLong();
    }
}

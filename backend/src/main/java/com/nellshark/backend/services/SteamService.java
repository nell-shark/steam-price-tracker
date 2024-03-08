package com.nellshark.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.exceptions.SteamApiException;
import com.nellshark.backend.exceptions.UnexpectedJsonStructureException;
import com.nellshark.backend.models.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamService {
    private static final String SUCCESS_FIELD = "success";
    private static final String DATA_FIELD = "data";
    private static final String PRICE_OVERVIEW_FIELD = "price_overview";
    private static final String FINAL_FORMATTED_FIELD = "final_formatted";

    private static final int STEAM_API_REQUEST_LIMIT = 50;
    private static final AtomicInteger countOfSteamRequests = new AtomicInteger(0);

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

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
        if (countOfSteamRequests.incrementAndGet() <= STEAM_API_REQUEST_LIMIT) {
            return;
        }

        try {
            TimeUnit.MINUTES.sleep(5);
            countOfSteamRequests.set(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
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

    public String getNewGamePrice(long gameId, @NonNull Currency currency) throws SteamApiException {
        URL url = buildSteamPriceOverviewUrl(gameId, currency);
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGamePriceNode(jsonNode);
    }

    private URL buildSteamPriceOverviewUrl(long gameId, @NonNull Currency currency) {
        try {
            URIBuilder uri = new URIBuilder("https://store.steampowered.com/api/appdetails");
            uri.addParameter("appids", String.valueOf(gameId));
            uri.addParameter("cc", currency.getCountryCode());
            uri.addParameter("filters", "price_overview");
            return uri.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("Error building URL", e);
        }
    }

    private String parseGamePriceNode(@NonNull JsonNode jsonNode) throws SteamApiException {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode mainNode = jsonNode.elements().next();
        if (!mainNode.path(SUCCESS_FIELD).asBoolean()) {
            throw new SteamApiException("Steam API request was not successful");
        }

        return mainNode
                .path(DATA_FIELD)
                .path(PRICE_OVERVIEW_FIELD)
                .path(FINAL_FORMATTED_FIELD)
                .asText();
    }
}

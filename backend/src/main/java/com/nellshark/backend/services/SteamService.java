package com.nellshark.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.exceptions.UnexpectedJsonStructureException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.GameType;
import com.nellshark.backend.models.Metacritic;
import com.nellshark.backend.models.OperatingSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.EnumUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.nellshark.backend.models.OperatingSystem.WINDOWS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamService {
    private static final short STEAM_API_REQUEST_LIMIT = 150;
    private static final DateTimeFormatter DATE_TIME_FORMATTER;
    private static short countOfSteamRequests = 0;

    static {
        DateTimeFormatter pattern1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
        DateTimeFormatter pattern2 = DateTimeFormatter.ofPattern("d MMM, yyyy");

        DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .appendOptional(pattern1)
                .appendOptional(pattern2)
                .toFormatter();
    }

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
            throw new RuntimeException("Error building URL", e);
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
        if (++countOfSteamRequests <= STEAM_API_REQUEST_LIMIT) {
            return;
        }

        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(5));
            countOfSteamRequests = 0;
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

    private List<Long> parseGameIds(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode appsArray = jsonNode
                .path("applist")
                .path("apps");

        if (!appsArray.isArray()) {
            throw new UnexpectedJsonStructureException("Error parsing JSON response. JSON must be an array");
        }

        return StreamSupport.stream(appsArray.spliterator(), true)
                .filter(JsonNode::isObject)
                .filter(app -> !app.isEmpty())
                .filter(app -> isNotBlank(app.path("name").asText()))
                .filter(app -> app.hasNonNull("appid"))
                .map(app -> app.get("appid").asLong())
                .toList();
    }

    @Nullable
    public Game getGameInfo(long gameId) {
        log.info("Getting a game info: id={}", gameId);
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
            throw new RuntimeException("Error building URL", e);
        }
    }

    @Nullable
    private Game parseGameInfo(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode mainNode = jsonNode.elements().next();
        if (!mainNode.path("success").asBoolean()) {
            return null;
        }

        JsonNode dataNode = mainNode.path("data");
        JsonNode releaseDateNode = dataNode.path("release_date");
        String gameTypeText = dataNode.path("type").asText().toUpperCase();

        if (!EnumUtils.isValidEnumIgnoreCase(GameType.class, gameTypeText)
                || dataNode.path("is_free").asBoolean()
                || releaseDateNode.path("coming_soon").asBoolean()) {
            return null;
        }

        long id = dataNode.get("steam_appid").asLong();
        String name = dataNode.path("name").asText();
        GameType gameType = GameType.valueOf(gameTypeText);
        String headerImage = dataNode.path("header_image").asText();
        String shortDescription = stripToNull(
                dataNode.path("short_description").asText()
        );
        String developers = stripToNull(
                dataNode.path("developers").asText()
        );
        String website = stripToNull(
                dataNode.path("website").asText()
        );
        String releaseDateText = releaseDateNode.path("date").asText();
        LocalDate releaseDate = LocalDate.parse(releaseDateText, DATE_TIME_FORMATTER);

        JsonNode supportInfoNode = dataNode.path("support_info");
        String email = stripToNull(
                supportInfoNode.path("email").asText()
        );

        Map<OperatingSystem, String> operatingSystemRequirements = parseOperatingSystemRequirementsList(dataNode);

        Metacritic metacritic = parseMetacritic(dataNode);

        return new Game(
                id,
                name,
                gameType,
                headerImage,
                releaseDate,
                operatingSystemRequirements,
                shortDescription,
                developers,
                website,
                email,
                metacritic
        );
    }

    private Map<OperatingSystem, String> parseOperatingSystemRequirementsList(@NonNull JsonNode dataNode) {
        JsonNode platformsNode = dataNode.path("platforms");

        return Stream.of(OperatingSystem.values())
                .filter(system -> platformsNode.path(system.name().toLowerCase()).asBoolean())
                .collect(HashMap::new,
                        (map, system) -> map.put(system, parseMinOperatingSystemRequirements(dataNode, system)),
                        HashMap::putAll
                );
    }

    private String parseMinOperatingSystemRequirements(@NonNull JsonNode dataNode,
                                                       OperatingSystem operatingSystem) {
        String requirementsPath = operatingSystem == WINDOWS
                ? "pc_requirements"
                : operatingSystem.name().toLowerCase() + "_requirements";

        return stripToNull(dataNode
                .path(requirementsPath)
                .path("minimum")
                .asText()
        );
    }

    @Nullable
    private Metacritic parseMetacritic(@NonNull JsonNode dataNode) {
        JsonNode metacriticNode = dataNode.path("metacritic");

        if (metacriticNode.isMissingNode()) {
            return null;
        }

        int score = metacriticNode.path("score").asInt();
        String url = stripToNull(
                metacriticNode.path("url").asText()
        );
        return new Metacritic(score, url);
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
            throw new RuntimeException("Error building URL", e);
        }
    }

    private long parseGamePrice(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode mainNode = jsonNode.elements().next();
        if (!mainNode.path("success").asBoolean()) {
            throw new GameNotFoundException("Game doesn't exist");
        }

        return mainNode
                .path("data")
                .path("price_overview")
                .path("final")
                .asLong();
    }
}

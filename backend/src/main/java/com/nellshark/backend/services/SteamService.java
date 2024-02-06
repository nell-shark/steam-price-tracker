package com.nellshark.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nellshark.backend.exceptions.SteamApiException;
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
import org.apache.commons.lang3.StringUtils;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.nellshark.backend.models.OperatingSystem.WINDOWS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamService {
    private static final String APPLIST_FIELD = "applist";
    private static final String APPS_FIELD = "apps";
    private static final String NAME_FIELD = "name";
    private static final String APPID_FIELD = "appid";
    private static final String SUCCESS_FIELD = "success";
    private static final String IS_FREE_FIELD = "is_free";
    private static final String DATA_FIELD = "data";
    private static final String TYPE_FIELD = "type";
    private static final String HEADER_IMAGE_FIELD = "header_image";
    private static final String SHORT_DESCRIPTION_FIELD = "short_description";
    private static final String WEBSITE_FIELD = "website";
    private static final String RELEASE_DATE_FIELD = "release_date";
    private static final String COMING_SOON_FIELD = "coming_soon";
    private static final String DATE_FIELD = "date";
    private static final String DEVELOPERS_FIELD = "developers";
    private static final String PUBLISHERS_FIELD = "publishers";
    private static final String PLATFORMS_FIELD = "platforms";
    private static final String MINIMUM_FIELD = "minimum";
    private static final String METACRITIC_FIELD = "metacritic";
    private static final String SCORE_FIELD = "score";
    private static final String URL_FIELD = "url";
    private static final String WINDOWS_REQUIREMENTS_FIELD = "pc_requirements";
    private static final String PRICE_OVERVIEW_FIELD = "price_overview";
    private static final String FINAL_FIELD = "final";

    private static final short STEAM_API_REQUEST_LIMIT = 150;
    private static final AtomicInteger countOfSteamRequests = new AtomicInteger();
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

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
    private final GameBlockedService gameBlockedService;

    public List<Long> getAllSteamGameIds() {
        log.info("Getting all steam games id");
        URL url = buildSteamAppListUrl();
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGameIdsNode(jsonNode);
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
        if (countOfSteamRequests.incrementAndGet() <= STEAM_API_REQUEST_LIMIT) {
            return;
        }

        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(5));
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

    private List<Long> parseGameIdsNode(@NonNull JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new UnexpectedJsonStructureException("JSON response is not a JSON object");
        }

        JsonNode appsArray = jsonNode
                .path(APPLIST_FIELD)
                .path(APPS_FIELD);

        if (!appsArray.isArray()) {
            throw new UnexpectedJsonStructureException("Error parsing JSON response. JSON must be an array");
        }

        return StreamSupport.stream(appsArray.spliterator(), true)
                .filter(JsonNode::isObject)
                .filter(app -> !app.isEmpty())
                .filter(app -> isNotBlank(app.path(NAME_FIELD).asText()))
                .filter(app -> app.hasNonNull(APPID_FIELD))
                .map(app -> app.get(APPID_FIELD).asLong())
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

        long id = Long.parseLong(jsonNode.fieldNames().next());

        JsonNode mainNode = jsonNode.elements().next();
        if (!mainNode.path(SUCCESS_FIELD).asBoolean()) {
            gameBlockedService.addGameToBlockList(id);
            return null;
        }

        JsonNode dataNode = mainNode.path(DATA_FIELD);
        if (dataNode.path(IS_FREE_FIELD).asBoolean()) {
            return null;
        }

        String name = dataNode.path(NAME_FIELD).asText();
        String gameTypeText = dataNode.path(TYPE_FIELD).asText().toUpperCase();
        if (!EnumUtils.isValidEnumIgnoreCase(GameType.class, gameTypeText)) {
            return null;
        }
        GameType gameType = GameType.valueOf(gameTypeText);
        String headerImage = dataNode.path(HEADER_IMAGE_FIELD).asText();
        LocalDate releaseDate = parseReleaseDateNode(dataNode);
        Map<OperatingSystem, String> operatingSystemRequirements = parseOperatingSystemRequirementsNode(dataNode);

        String shortDescription = stripToNull(dataNode.path(SHORT_DESCRIPTION_FIELD).textValue());
        String developers = parseDevelopersNode(dataNode);
        String publishers = parsePublishersNode(dataNode);
        String website = stripToNull(dataNode.path(WEBSITE_FIELD).textValue());
        Metacritic metacritic = parseMetacriticNode(dataNode);

        return new Game(
                id,
                name,
                gameType,
                headerImage,
                operatingSystemRequirements,
                shortDescription,
                releaseDate,
                developers,
                publishers,
                website,
                metacritic
        );
    }

    @Nullable
    private LocalDate parseReleaseDateNode(@NonNull JsonNode dataNode) {
        JsonNode releaseDateNode = dataNode.path(RELEASE_DATE_FIELD);
        String date = releaseDateNode.path(DATE_FIELD).asText();

        if (releaseDateNode.path(COMING_SOON_FIELD).asBoolean()
                || StringUtils.isBlank(date)) {
            return null;
        }

        return LocalDate.parse(date, DATE_TIME_FORMATTER);
    }

    @Nullable
    private String parseDevelopersNode(@NonNull JsonNode dataNode) {
        JsonNode developersNode = dataNode.path(DEVELOPERS_FIELD);
        return parseDevelopersOrPublishersNode(developersNode);
    }

    @Nullable
    private String parsePublishersNode(@NonNull JsonNode dataNode) {
        JsonNode developersNode = dataNode.path(PUBLISHERS_FIELD);
        return parseDevelopersOrPublishersNode(developersNode);
    }

    @Nullable
    private String parseDevelopersOrPublishersNode(@NonNull JsonNode jsonNode) {
        if (jsonNode.isMissingNode()
                || !jsonNode.isArray()
                || jsonNode.isEmpty()
                || (jsonNode.size() == 1 && StringUtils.isBlank(jsonNode.get(0).asText()))) {
            return null;
        }

        return StreamSupport.stream(jsonNode.spliterator(), true)
                .map(JsonNode::textValue)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
    }

    private Map<OperatingSystem, String> parseOperatingSystemRequirementsNode(@NonNull JsonNode dataNode) {
        JsonNode platformsNode = dataNode.path(PLATFORMS_FIELD);

        return Stream.of(OperatingSystem.values())
                .filter(system -> platformsNode.path(system.name().toLowerCase()).asBoolean())
                .collect(HashMap::new,
                        (map, system) -> map.put(system, parseOperatingSystemRequirements(dataNode, system)),
                        HashMap::putAll
                );
    }

    @Nullable
    private String parseOperatingSystemRequirements(@NonNull JsonNode dataNode,
                                                    @NonNull OperatingSystem operatingSystem) {
        String requirementsPath = operatingSystem == WINDOWS
                ? WINDOWS_REQUIREMENTS_FIELD
                : operatingSystem.name().toLowerCase() + "_requirements";

        return stripToNull(
                dataNode.path(requirementsPath)
                        .path(MINIMUM_FIELD)
                        .textValue()
        );
    }

    @Nullable
    private Metacritic parseMetacriticNode(@NonNull JsonNode dataNode) {
        JsonNode metacriticNode = dataNode.path(METACRITIC_FIELD);

        if (metacriticNode.isMissingNode()) {
            return null;
        }

        Integer score = metacriticNode.path(SCORE_FIELD).intValue();
        String url = stripToNull(metacriticNode.path(URL_FIELD).textValue());
        return new Metacritic(score, url);
    }

    public long getNewGamePrice(long gameId, @NonNull CountryCode countryCode) throws SteamApiException {
        URL url = buildSteamPriceOverviewUrl(gameId, countryCode);
        JsonNode jsonNode = fetchSteamApiData(url);
        return parseGamePriceNode(jsonNode);
    }

    private URL buildSteamPriceOverviewUrl(long gameId, @NonNull CountryCode countryCode) {
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

    private long parseGamePriceNode(@NonNull JsonNode jsonNode) throws SteamApiException {
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
                .path(FINAL_FIELD)
                .asLong();
    }
}

package dev.rafandoo.finv.provider.yahoo.request.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.cup.object.export.ExportData;
import dev.rafandoo.cup.object.export.strategy.Json;
import dev.rafandoo.cup.object.loader.ObjectLoader;
import dev.rafandoo.cup.object.tree.ObjectTree;
import dev.rafandoo.cup.request.HttpRequester;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ProviderRequestException;
import dev.rafandoo.finv.provider.yahoo.YahooMetadata;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooApiVersion;
import dev.rafandoo.finv.provider.yahoo.request.YahooRequestBuilder;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static dev.rafandoo.finv.enums.Parameters.REQUEST_TIMEOUT;
import static dev.rafandoo.finv.enums.Parameters.USER_AGENT;

/**
 * AuthManager is responsible for managing the authentication state required to access Yahoo's financial data APIs.
 * It handles obtaining and refreshing authentication cookies and crumbs.
 */
@Slf4j
@UtilityClass
public final class AuthManager {

    private static volatile AuthState state;
    private static ObjectMapper mapper;

    private static final Path CACHE_FILE = Path.of(System.getProperty("java.io.tmpdir"), ".finv", "yahoo_auth.json");

    /**
     * Retrieves the current authentication state. If the state is not valid or not present,
     * it will attempt to load it from disk.
     * If loading from disk fails or the loaded state is still not valid, it will refresh the
     * authentication by obtaining a new cookie and crumb.
     *
     * @return the current valid authentication state.
     */
    public static synchronized AuthState getAuth() {
        if (state != null && state.isValid()) {
            return state;
        }

        loadFromDisk();

        if (state != null && state.isValid()) {
            return state;
        }

        refresh();
        return state;
    }

    /**
     * Refreshes the authentication by obtaining a new cookie and crumb from Yahoo.
     * It then updates the internal state and saves it to disk for future use.
     */
    public static synchronized void refresh() {
        log.info("#{} - Refreshing authentication...", YahooMetadata.METADATA.id());
        String cookie = obtainCookie();
        String crumb = obtainCrumb(cookie);
        Instant expiresAt = extractExpiry(cookie);

        state = new AuthState(cookie, crumb, expiresAt, Instant.now());

        saveToDisk();
    }

    /**
     * Obtains a new authentication cookie from Yahoo by sending a request to the appropriate endpoint.
     * It extracts the 'A3' cookie value and formats it for use in subsequent requests.
     *
     * @return the formatted cookie string containing the 'A3' value.
     * @throws ProviderRequestException if the cookie cannot be obtained or if the HTTP request fails.
     * @throws IllegalStateException    if the 'A3' cookie is not found in the response.
     */
    private static String obtainCookie() {
        try (HttpResponse response = sendRequest(
            YahooRequestBuilder.builder()
                .host(YahooHost.FC)
                .create()
                .toString(),
            Map.of("User-Agent", USER_AGENT.get().asString())
        )) {
            String cookie = Optional.ofNullable(response.getCookies())
                .map(cookies -> cookies.get("A3"))
                .map(value -> "A3=" + value)
                .orElseThrow(() -> new IllegalStateException("Failed to obtain Yahoo cookie. 'A3' cookie not found."));

            log.debug("#{} - Obtained cookie: {}", YahooMetadata.METADATA.id(), cookie);
            return cookie;
        } catch (IOException e) {
            throw new ProviderRequestException("Failed to obtain Yahoo cookie.", e);
        }
    }

    /**
     * Obtains a new crumb from Yahoo by sending a request to the appropriate endpoint with the provided cookie.
     * It extracts the crumb value from the response body.
     *
     * @param cookie the authentication cookie to be included in the request headers.
     * @return the crumb value obtained from Yahoo.
     * @throws ProviderRequestException if the crumb cannot be obtained or if the HTTP request fails.
     * @throws IllegalStateException    if the HTTP response indicates a failure or if the crumb cannot be extracted from the response body.
     */
    private static String obtainCrumb(String cookie) {
        try (HttpResponse response = sendRequest(
            YahooRequestBuilder.builder()
                .host(YahooHost.QUERY2)
                .version(YahooApiVersion.V1)
                .service(YahooService.GET_CRUMB)
                .create()
                .toString(),
            Map.of(
                "User-Agent", USER_AGENT.get().asString(),
                "Cookie", cookie
            )
        )) {
            if (response.isNotSuccessful()) {
                throw new IllegalStateException("Failed to obtain Yahoo crumb. HTTP status: " + response.getStatusCode());
            }

            String crumb = response.getBodyAsString().trim();
            log.debug("#{} - Crumb obtained: {}", YahooMetadata.METADATA.id(), crumb);
            return crumb;
        } catch (IOException e) {
            throw new ProviderRequestException("Failed to obtain Yahoo crumb.", e);
        }
    }

    /**
     * Sends an HTTP GET request to the specified URL with the provided headers and returns the response.
     *
     * @param url     the URL to which the request is sent.
     * @param headers a map of header names and values to be included in the request.
     * @return the HttpResponse received from the server.
     * @throws IOException if an I/O error occurs during the request.
     */
    private static HttpResponse sendRequest(String url, Map<String, Object> headers) throws IOException {
        return HttpRequester.builder()
            .url(url)
            .connectionTimeout(REQUEST_TIMEOUT.get().asInteger())
            .headers(headers)
            .get();
    }

    /**
     * Extracts the expiry time from the provided cookie string.
     * It looks for the 'expires' attribute in the cookie and parses it as a date.
     * If the expiry cannot be found or parsed, it defaults to 1 hour from the current time.
     *
     * @param cookie the cookie string from which to extract the expiry time.
     * @return the Instant representing the expiry time of the cookie.
     */
    private static Instant extractExpiry(String cookie) {
        String[] parts = cookie.split(";");
        for (String part : parts) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length == 2 && kv[0].equalsIgnoreCase("expires")) {
                try {
                    return ZonedDateTime
                        .parse(kv[1].trim(), DateTimeFormatter.RFC_1123_DATE_TIME)
                        .toInstant();
                } catch (Exception e) {
                    log.warn(
                        "#{} - Failed to parse cookie expiry date: {}",
                        YahooMetadata.METADATA.id(),
                        kv[1].trim(),
                        e
                    );
                }
            }
        }
        // Default to 1 hour if expiry not found or parsing fails
        return Instant.now().plusSeconds(3600);
    }

    /**
     * Retrieves the ObjectMapper instance used for JSON serialization and deserialization.
     * It initializes the mapper if it has not been created yet, configuring it with the
     * necessary modules and settings for handling Java time types.
     *
     * @return the configured ObjectMapper instance.
     */
    private static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = JacksonMapperFactory.json();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return mapper;
    }

    /**
     * Saves the current authentication state to disk as a JSON file.
     */
    private static void saveToDisk() {
        ExportData export = ExportData.of(new Json(getMapper()));
        try {
            Path parent = CACHE_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String content = export.exportToString(state);
            Files.writeString(
                CACHE_FILE,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            log.info("#{} - Authentication state saved to disk.", YahooMetadata.METADATA.id());
        } catch (Exception e) {
            log.warn("#{} - Failed to save authentication state to disk.", YahooMetadata.METADATA.id(), e);
        }
    }

    /**
     * Loads the authentication state from disk if the cache file exists.
     * It deserializes the JSON content into an AuthState object and updates the internal state.
     */
    private static void loadFromDisk() {
        if (Files.exists(CACHE_FILE)) {
            try {
                ObjectTree objectTree = ObjectLoader.from(CACHE_FILE, dev.rafandoo.cup.object.source.Json.class);
                objectTree.setMapper(getMapper());
                state = objectTree.as(AuthState.class);
                log.info("#{} - Authentication state loaded from disk.", YahooMetadata.METADATA.id());
            } catch (Exception e) {
                log.warn("#{} - Failed to load authentication state from disk.", YahooMetadata.METADATA.id(), e);
            }
        }
    }
}

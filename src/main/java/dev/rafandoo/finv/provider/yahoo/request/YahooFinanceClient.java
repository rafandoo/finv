package dev.rafandoo.finv.provider.yahoo.request;

import dev.rafandoo.cup.request.HttpRequester;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.yahoo.YahooMetadata;
import dev.rafandoo.finv.provider.yahoo.request.auth.AuthState;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.YahooRequest;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static dev.rafandoo.finv.enums.Parameters.USER_AGENT;

/**
 * Client for executing Yahoo Finance API requests. Handles authentication, request construction, and response parsing.
 */
@Slf4j
public final class YahooFinanceClient {

    private final AuthState authState;

    public YahooFinanceClient(AuthState authState) {
        this.authState = authState;
    }

    /**
     * Executes the given Yahoo Finance API request and returns the parsed response.
     *
     * @param request the Yahoo Finance API request end execute.
     * @param <T>     the type of the response data.
     * @return an Optional containing the parsed response data, or empty if the request failed or parsing failed.
     */
    public <T> Optional<T> execute(YahooRequest<T> request) {
        Map<String, Object> headers = Map.of(
            "Cookie", this.authState.cookie(),
            "Accept", "application/json",
            "User-Agent", USER_AGENT.get().asString()
        );

        UrlBuilder urlBuilder = request.url();

        if (request.requiresCrumb()) {
            urlBuilder.addParameter("crumb", this.authState.crumb());
            urlBuilder.addParameter("corsDomain", YahooHost.FINANCE.host());
        }

        String url = urlBuilder.toString();

        log.debug("#{} - Executing request: {}",
            YahooMetadata.METADATA.id(),
            url
        );

        HttpResponse response = null;
        try {
            response = HttpRequester.builder()
                .url(url)
                .headers(headers)
                .get();
        } catch (IOException e) {
            log.error("#{} - Request failed.",
                YahooMetadata.METADATA.id(),
                e
            );
        }

        if (response != null) {
            try {
                return request.parser().parse(response);
            } catch (ParseException e) {
                log.error("#{} - Failed to parse response.",
                    YahooMetadata.METADATA.id(),
                    e
                );
            }
        }
        return Optional.empty();
    }
}

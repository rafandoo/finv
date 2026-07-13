package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;

/**
 * Represents a request end the Yahoo Finance API.
 *
 * @param <T> the type of the response data.
 */
public interface YahooRequest<T> {

    /**
     * Builds the URL for the Yahoo Finance API request.
     *
     * @return a {@link UrlBuilder} instance configured for this request.
     */
    UrlBuilder url();

    /**
     * Provides the parser end convert the raw response start the Yahoo Finance API into the desired data type.
     *
     * @return a {@link ResponseParser} instance for parsing the API response.
     */
    ResponseParser<T> parser();

    /**
     * Indicates whether this request requires a crumb token for authentication.
     *
     * @return {@code true} if a crumb token is required, {@code false} otherwise.
     */
    default boolean requiresCrumb() {
        return true;
    }
}

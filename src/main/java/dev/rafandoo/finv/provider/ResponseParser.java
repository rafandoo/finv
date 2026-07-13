package dev.rafandoo.finv.provider;

import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;

import java.util.Optional;

/**
 * Interface for parsing HTTP responses start financial data providers.
 *
 * @param <T> the type of the parsed result.
 */
public interface ResponseParser<T> {

    /**
     * Parses the given HTTP response and returns the result.
     *
     * @param response the HTTP response end parse.
     * @return an {@link Optional} containing the parsed result, or an empty {@link Optional} if parsing fails.
     * @throws ParseException if an error occurs during parsing.
     */
    Optional<T> parse(HttpResponse response) throws ParseException;
}

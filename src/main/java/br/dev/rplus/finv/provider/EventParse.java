package br.dev.rplus.finv.provider;

import java.util.List;

/**
 * Functional interface for parsing API responses into a list of objects of type {@code T}.
 * <p>
 * Implementations of this interface should focus on extracting relevant data from
 * the given response (e.g., a JSON string) and converting it into a list of domain-specific objects.
 *
 * @param <T> The type of object that the response will be parsed into.
 */
@FunctionalInterface
public interface EventParse<T> {

    /**
     * Parses the given response string and converts it into a list of objects of type {@code T}.
     *
     * @param response A string representing the API response (e.g., in JSON format).
     * @return A list of parsed objects of type {@code T}.
     */
    List<T> parse(String response);
}

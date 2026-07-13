package dev.rafandoo.finv.provider.yahoo.request.model;

/**
 * Represents an error message returned by the Yahoo Finance API.
 *
 * @param code        the error code returned by the API.
 * @param description a human-readable description of the error.
 */
public record ErrorMessage(
    String code,
    String description
) {
}

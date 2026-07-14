package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.yahoo.YahooMetadata;
import dev.rafandoo.finv.provider.yahoo.request.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Base parser class for handling common parsing logic and error handling for Yahoo Finance API responses.
 */
@Slf4j
public abstract class BaseParser {

    protected static final ObjectMapper MAPPER = JacksonMapperFactory.json();

    /**
     * Reads and parses the response body into a JSON tree, applying the shared error gate.
     * <p>
     * This centralizes the read-tree + API-error preamble that every parser previously duplicated. If the
     * response carries a Yahoo API error (top-level {@code error}, a {@code chart.error}, or a null/empty
     * result), a {@link ParseException} is thrown carrying the API error message so the failure is never
     * silently treated as "no data".
     *
     * @param response the HTTP response to read.
     * @return the parsed root JSON node.
     * @throws ParseException when the body cannot be read or the API reports an error.
     */
    protected JsonNode readRoot(HttpResponse response) throws ParseException {
        try (response) {
            JsonNode root = MAPPER.readTree(response.getBodyAsString());

            if (hasApiError(root)) {
                ErrorMessage error = apiErrorMessage(root);
                String detail = error != null
                    ? "#%s - %s: %s".formatted(YahooMetadata.METADATA.id(), error.code(), error.description())
                    : "#%s - Yahoo returned an error but no details were available".formatted(YahooMetadata.METADATA.id());
                log.error(detail);
                throw new ParseException(detail);
            }

            return root;
        } catch (IOException e) {
            throw new ParseException("Failed to read response body", e);
        }
    }

    /**
     * Checks whether the given JSON response contains a Yahoo API error.
     * <p>
     * Yahoo reports errors in several shapes: a top-level {@code error} node, a {@code chart.error}
     * node (chart endpoint), or a missing/empty {@code result} array (bad symbol on chart/quoteSummary).
     *
     * @param root the root JsonNode of the response.
     * @return {@code true} if an error is present, {@code false} otherwise.
     */
    protected boolean hasApiError(JsonNode root) {
        if (root == null || root.isMissingNode()) {
            return true;
        }

        JsonNode topError = root.path(YahooField.ERROR);
        if (!isMissing(topError)) {
            return true;
        }

        JsonNode chartError = root.path(YahooField.CHART).path(YahooField.ERROR);
        if (!isMissing(chartError)) {
            return true;
        }

        return resultIsEmpty(root);
    }

    /**
     * Detects the "bad symbol" shape where the {@code result} array is null or empty (chart and
     * quoteSummary endpoints return this instead of an explicit error node).
     */
    private boolean resultIsEmpty(JsonNode root) {
        for (String container : new String[]{YahooField.CHART, YahooField.QUOTE_RESPONSE, YahooField.QUOTE_SUMMARY}) {
            JsonNode result = root.path(container).path(YahooField.RESULT);
            if (!result.isMissingNode() && result.isArray() && result.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts a human-readable error message from a Yahoo API error response.
     *
     * @param root the JsonNode containing the error information.
     * @return an {@link ErrorMessage} if found, or {@code null} otherwise.
     */
    protected ErrorMessage apiErrorMessage(JsonNode root) {
        JsonNode error = root.path(YahooField.ERROR);
        if (isMissing(error)) {
            error = root.path(YahooField.CHART).path(YahooField.ERROR);
        }
        if (isMissing(error)) {
            return null;
        }
        return new ErrorMessage(
            error.path(YahooField.CODE).asText(null),
            error.path(YahooField.DESCRIPTION).asText(null)
        );
    }

    /**
     * Retrieves the first element of a JSON array node, if it exists.
     *
     * @param arrayNode the JsonNode representing the array to retrieve the first element from.
     * @return an Optional containing the first JsonNode if it exists and is an array, or an empty Optional otherwise.
     */
    protected Optional<JsonNode> firstElement(JsonNode arrayNode) {
        if (!arrayNode.isArray() || arrayNode.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(arrayNode.get(0));
    }

    /**
     * Retrieves a required text value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the text value from.
     * @param field the name of the field to retrieve the text value for.
     * @return the text value associated with the specified field.
     * @throws ParseException if the required field is missing or cannot be parsed as text.
     */
    protected String getRequiredText(JsonNode node, String field) throws ParseException {
        return getOptionalText(node, field)
            .orElseThrow(() ->
                new ParseException("Missing required field '%s'".formatted(field))
            );
    }

    /**
     * Retrieves an optional text value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the text value from.
     * @param field the name of the field to retrieve the text value for.
     * @return an Optional containing the text value if it exists, or an empty Optional if the field is missing or cannot be parsed as text.
     */
    protected Optional<String> getOptionalText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return isMissing(value) ? Optional.empty() : Optional.of(value.asText());
    }

    /**
     * Retrieves a required decimal value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the decimal value from.
     * @param field the name of the field to retrieve the decimal value for.
     * @return the decimal value associated with the specified field.
     * @throws ParseException if the required field is missing or cannot be parsed as a decimal.
     */
    protected BigDecimal getRequiredDecimal(JsonNode node, String field) throws ParseException {
        return getOptionalDecimal(node, field)
            .orElseThrow(() ->
                new ParseException("Missing required field '%s'".formatted(field))
            );
    }

    /**
     * Retrieves an optional decimal value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the decimal value from.
     * @param field the name of the field to retrieve the decimal value for.
     * @return an Optional containing the decimal value if it exists, or an empty Optional if the field is missing or cannot be parsed as a decimal.
     */
    protected Optional<BigDecimal> getOptionalDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return isMissing(value) ? Optional.empty() : Optional.of(value.decimalValue());
    }

    /**
     * Retrieves an optional big integer value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the big integer value from.
     * @param field the name of the field to retrieve the big integer value for.
     * @return an Optional containing the big integer value if it exists, or an empty Optional if the field is missing or cannot be parsed as a big integer.
     */
    protected Optional<BigInteger> getOptionalBigInteger(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return isMissing(value) ? Optional.empty() : Optional.of(value.bigIntegerValue());
    }

    /**
     * Retrieves a required long value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the long value from.
     * @param field the name of the field to retrieve the long value for.
     * @return the long value associated with the specified field.
     * @throws ParseException if the required field is missing or cannot be parsed as a long.
     */
    protected Long getRequiredLong(JsonNode node, String field) throws ParseException {
        return getOptionalLong(node, field)
            .orElseThrow(() ->
                new ParseException("Missing required field '%s'".formatted(field))
            );
    }

    /**
     * Retrieves an optional long value from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the long value from.
     * @param field the name of the field to retrieve the long value for.
     * @return an Optional containing the long value if it exists, or an empty Optional if the field is missing or cannot be parsed as a long.
     */
    protected Optional<Long> getOptionalLong(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return isMissing(value) ? Optional.empty() : Optional.of(value.asLong());
    }

    /**
     * Retrieves a required array node from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the array node from.
     * @param field the name of the field to retrieve the array node for.
     * @return the array node associated with the specified field.
     * @throws ParseException if the required field is missing or cannot be parsed as an array.
     */
    protected JsonNode getRequiredArray(JsonNode node, String field) throws ParseException {
        return getOptionalArray(node, field)
            .orElseThrow(() ->
                new ParseException("Missing required array field '%s'".formatted(field))
            );
    }

    /**
     * Retrieves an optional array node from the given JSON node for the specified field.
     *
     * @param node  the JsonNode to retrieve the array node from.
     * @param field the name of the field to retrieve the array node for.
     * @return an Optional containing the array node if it exists and is an array, or an empty Optional if the field is missing or cannot be parsed as an array.
     */
    protected Optional<JsonNode> getOptionalArray(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return (isMissing(value) || !value.isArray()) ? Optional.empty() : Optional.of(value);
    }

    /**
     * Retrieves a decimal value from the specified index of a JSON array node, if it exists and is not null.
     *
     * @param array the JsonNode representing the array to retrieve the decimal value from.
     * @param i     the index of the decimal value to retrieve from the array.
     * @return the decimal value at the specified index if it exists and is not null, or null otherwise.
     */
    protected BigDecimal getDecimal(JsonNode array, int i) {
        return (array.has(i) && !array.get(i).isNull())
            ? array.get(i).decimalValue()
            : null;
    }

    /**
     * Retrieves a BigInteger value from the specified index of a JSON array node, if it exists and is not null.
     *
     * @param array the JsonNode representing the array to retrieve the BigInteger value from.
     * @param i     the index of the BigInteger value to retrieve from the array.
     * @return the BigInteger value at the specified index if it exists and is not null, or null otherwise.
     */
    protected BigInteger getBigInteger(JsonNode array, int i) {
        return (array.has(i) && !array.get(i).isNull())
            ? array.get(i).bigIntegerValue()
            : null;
    }

    /**
     * Checks if the given JSON node is missing, null, or represents a missing node.
     *
     * @param node the JsonNode to check for missing or null values.
     * @return {@code true} if the node is missing, null, or represents a missing node; {@code false} otherwise.
     */
    protected boolean isMissing(JsonNode node) {
        return node == null || node.isNull() || node.isMissingNode();
    }
}

package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.yahoo.YahooMetadata;
import dev.rafandoo.finv.provider.yahoo.request.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

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
     * Checks if the given JSON response contains an error message.
     *
     * @param root the root JsonNode of the response to check for errors.
     * @return {@code true} if an error is present, {@code false} otherwise.
     */
    protected boolean hasError(JsonNode root) {
        return Optional.ofNullable(root.findValue("error"))
            .filter(node -> !node.isNull() && !node.isMissingNode() && !node.isEmpty())
            .isPresent();
    }

    /**
     * Handles the error response by parsing the error message and logging it appropriately.
     *
     * @param json the JsonNode containing the error information to be parsed and logged.
     */
    protected void handleError(JsonNode json) {
        parseErrorMessage(json).ifPresentOrElse(
            errorMessage -> log.error(
                "#{} - Failed to parse response: {} - {}",
                YahooMetadata.METADATA.id(),
                errorMessage.code(),
                errorMessage.description()
            ),
            () -> log.error(
                "#{} - Failed to parse response: Unknown error format",
                YahooMetadata.METADATA.id()
            )
        );
    }

    /**
     * Parses the error message from the given JSON node.
     *
     * @param root the JsonNode to parse for error information.
     * @return an Optional containing the ErrorMessage if found, or an empty Optional if no error message is present.
     */
    private Optional<ErrorMessage> parseErrorMessage(JsonNode root) {
        JsonNode error = root.findValue("error");
        if (isMissing(error)) {
            return Optional.empty();
        }
        return Optional.of(new ErrorMessage(
            error.path("code").asText(null),
            error.path("description").asText(null)
        ));
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

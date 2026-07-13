package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteSummaryPrice;

import java.io.IOException;
import java.util.Optional;

/**
 * Parser for extracting price data start Yahoo Finance API quote summary responses.
 */
public class QuoteSummaryPriceParser extends BaseParser implements ResponseParser<QuoteSummaryPrice> {

    @Override
    public Optional<QuoteSummaryPrice> parse(HttpResponse response) throws ParseException {
        try (response) {
            JsonNode root = MAPPER.readTree(response.getBodyAsString());

            if (hasError(root)) {
                handleError(root);
                return Optional.empty();
            }

            return this.extractPriceNode(root)
                .map(this::buildPrice);
        } catch (IOException e) {
            throw new ParseException("Failed to parse quote summary price data", e);
        }
    }

    /**
     * Extracts the price node from the quote summary response JSON structure.
     *
     * @param root the root JSON node of the response.
     * @return an Optional containing the price node if found, or empty if not found or if an error is present.
     */
    private Optional<JsonNode> extractPriceNode(JsonNode root) {
        JsonNode result = root.path("quoteSummary").path("result");

        return firstElement(result)
            .map(node -> node.path("price"))
            .filter(node -> !isMissing(node));
    }

    /**
     * Builds a QuoteSummaryPrice object from the given price JSON node.
     *
     * @param price the JSON node containing price data.
     * @return a {@link QuoteSummaryPrice} object populated with data from the JSON node.
     * @throws ParseException if required fields are missing or if there is an error during parsing.
     */
    private QuoteSummaryPrice buildPrice(JsonNode price) throws ParseException {
        return new QuoteSummaryPrice(
            getRequiredText(price, "symbol"),
            getOptionalText(price, "shortName").orElse(null),
            getOptionalText(price, "currency").orElse(null),
            getOptionalText(price, "exchangeName").orElse(null),
            getOptionalDecimal(price, "regularMarketPrice").orElse(null),
            getOptionalDecimal(price, "regularMarketDayHigh").orElse(null),
            getOptionalDecimal(price, "regularMarketDayLow").orElse(null),
            getOptionalDecimal(price, "regularMarketOpen").orElse(null),
            getOptionalDecimal(price, "regularMarketPreviousClose").orElse(null),
            getOptionalBigInteger(price, "regularMarketVolume").orElse(null),
            getOptionalDecimal(price, "regularMarketChange").orElse(null),
            getOptionalDecimal(price, "regularMarketChangePercent").orElse(null)
        );
    }
}

package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteSummaryPrice;

import java.time.Instant;
import java.util.Optional;

/**
 * Parser for extracting price data from Yahoo Finance API quote summary responses.
 */
public class QuoteSummaryPriceParser extends BaseParser implements ResponseParser<QuoteSummaryPrice> {

    @Override
    public Optional<QuoteSummaryPrice> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);

        return this.extractPriceNode(root)
            .map(this::buildPrice);
    }

    /**
     * Extracts the price node from the quote summary response JSON structure.
     *
     * @param root the root JSON node of the response.
     * @return an Optional containing the price node if found, or empty if not found or if an error is present.
     */
    private Optional<JsonNode> extractPriceNode(JsonNode root) {
        JsonNode result = root.path(YahooField.QUOTE_SUMMARY).path(YahooField.RESULT);

        return firstElement(result)
            .map(node -> node.path(YahooField.PRICE))
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
            getRequiredText(price, YahooField.SYMBOL),
            getOptionalText(price, YahooField.SHORT_NAME).orElse(null),
            getOptionalText(price, YahooField.CURRENCY).orElse(null),
            getOptionalText(price, YahooField.EXCHANGE_NAME).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_PRICE).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_DAY_HIGH).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_DAY_LOW).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_OPEN).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_PREVIOUS_CLOSE).orElse(null),
            getOptionalBigInteger(price, YahooField.REGULAR_MARKET_VOLUME).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_CHANGE).orElse(null),
            getOptionalDecimal(price, YahooField.REGULAR_MARKET_CHANGE_PERCENT).orElse(null),
            getOptionalLong(price, YahooField.REGULAR_MARKET_TIME)
                .map(Instant::ofEpochSecond)
                .orElse(null)
        );
    }
}

package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.model.ChartAggregate;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooCandle;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooDividend;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooSplit;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses the response from Yahoo Finance API for historical data, dividends, and splits into a ChartAggregate object.
 * The parser determines which type of data to extract based on the specified YahooEvent type.
 */
@Slf4j
public class FinanceChartParser extends BaseParser implements ResponseParser<ChartAggregate> {

    private final EventExtractor extractor;
    private final boolean includeAdjClose;

    public FinanceChartParser(YahooEvent event) {
        this(event, false);
    }

    public FinanceChartParser(YahooEvent event, boolean includeAdjClose) {
        this.extractor = switch (event) {
            case HISTORY -> this::extractHistory;
            case DIVIDEND -> this::extractDividends;
            case SPLIT -> this::extractSplits;
        };
        this.includeAdjClose = includeAdjClose;
    }

    @Override
    public Optional<ChartAggregate> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);

        Optional<JsonNode> firstResult = this.firstResult(root);
        if (firstResult.isEmpty()) {
            log.warn("No result found in response");
            return Optional.empty();
        }

        JsonNode result = firstResult.get();
        String symbol = this.extractSymbol(result);

        return Optional.of(this.extractor.extract(symbol, result));
    }

    /**
     * Extracts historical price data from the given JSON result and constructs a ChartAggregate containing a list of
     * YahooCandle objects.
     *
     * @param symbol the stock symbol for which the historical data is being extracted.
     * @param result the JsonNode containing the historical data to be parsed.
     * @return a ChartAggregate containing the extracted historical price data as a list of YahooCandle objects.
     * @throws ParseException if there is an error during parsing of the historical data from the JSON result.
     */
    private ChartAggregate extractHistory(String symbol, JsonNode result) throws ParseException {
        return new ChartAggregate(
            symbol,
            this.buildYahooCandleList(result),
            List.of(),
            List.of()
        );
    }

    /**
     * Extracts dividend data from the given JSON result and constructs a ChartAggregate containing a list of
     * YahooDividend objects.
     *
     * @param symbol the stock symbol for which the dividend data is being extracted.
     * @param result the JsonNode containing the dividend data to be parsed.
     * @return a ChartAggregate containing the extracted dividend data as a list of YahooDividend objects.
     * @throws ParseException if there is an error during parsing of the dividend data from the JSON result.
     */
    private ChartAggregate extractDividends(String symbol, JsonNode result) {
        return new ChartAggregate(
            symbol,
            List.of(),
            this.buildEventList(result, YahooField.DIVIDENDS, this::buildYahooDividend),
            List.of()
        );
    }

    /**
     * Extracts split data from the given JSON result and constructs a ChartAggregate containing a list of
     * YahooSplit objects.
     *
     * @param symbol the stock symbol for which the split data is being extracted.
     * @param result the JsonNode containing the split data to be parsed.
     * @return a ChartAggregate containing the extracted split data as a list of YahooSplit objects.
     * @throws ParseException if there is an error during parsing of the split data from the JSON result.
     */
    private ChartAggregate extractSplits(String symbol, JsonNode result) {
        return new ChartAggregate(
            symbol,
            List.of(),
            List.of(),
            this.buildEventList(result, YahooField.SPLITS, this::buildYahooSplit)
        );
    }

    /**
     * Builds a list of event objects (either dividends or splits) from the given JSON result based on the specified
     * node name and mapper function.
     *
     * @param result   the JsonNode containing the event data to be parsed.
     * @param nodeName the name of the node in the JSON where the event data is located (e.g., "dividends" or "splits").
     * @param mapper   a function that maps a JsonNode representing an individual event to an instance of type T
     *                 (e.g., YahooDividend or YahooSplit).
     * @param <T>      the type of event object being extracted (e.g., YahooDividend or YahooSplit).
     * @return a list of event objects extracted from the JSON result, or an empty list if no valid events are found.
     */
    private <T> List<T> buildEventList(JsonNode result, String nodeName, EventNodeMapper<T> mapper) {
        JsonNode events = result.path(YahooField.EVENTS).path(nodeName);

        if (isMissing(events) || !events.isObject()) {
            return List.of();
        }

        List<T> list = new ArrayList<>();

        events.properties().forEach(entry -> {
            try {
                list.add(mapper.map(entry.getValue()));
            } catch (ParseException e) {
                log.warn("Failed parsing {} event", nodeName, e);
            }
        });

        return list;
    }

    /**
     * Builds a list of YahooCandle objects from the given JSON result by extracting the timestamp and quote data.
     *
     * @param result the JsonNode containing the historical price data to be parsed.
     * @return a list of YahooCandle objects representing the historical price data extracted from the JSON result.
     * @throws ParseException if there is an error during parsing of the historical price data from the JSON result.
     */
    private List<YahooCandle> buildYahooCandleList(JsonNode result) throws ParseException {
        JsonNode timestamps = getRequiredArray(result, YahooField.TIMESTAMP);

        JsonNode quote = firstElement(result.path(YahooField.INDICATORS).path(YahooField.QUOTE))
            .filter(node -> !isMissing(node))
            .orElseThrow(() ->
                new ParseException("Failed to parse historical data response: No quote data found")
            );

        JsonNode adjClose = this.adjCloseArray(result);

        List<YahooCandle> list = new ArrayList<>(timestamps.size());

        for (int i = 0; i < timestamps.size(); i++) {
            list.add(
                new YahooCandle(
                    Instant.ofEpochSecond(timestamps.get(i).asLong()),
                    getDecimal(quote.path(YahooField.OPEN), i),
                    getDecimal(quote.path(YahooField.HIGH), i),
                    getDecimal(quote.path(YahooField.LOW), i),
                    getDecimal(quote.path(YahooField.CLOSE), i),
                    getBigInteger(quote.path(YahooField.VOLUME), i),
                    this.includeAdjClose ? this.adjCloseAt(adjClose, i) : null
                )
            );
        }
        return list;
    }

    /**
     * Resolves the {@code adjclose} array from the indicators node, or {@code null} when adjusted close
     * was not requested or is absent.
     *
     * @param result the JsonNode containing the historical data to be parsed.
     * @return the adjclose array node, or {@code null} when not present.
     */
    private JsonNode adjCloseArray(JsonNode result) {
        if (!this.includeAdjClose) {
            return null;
        }
        return firstElement(result.path(YahooField.INDICATORS).path(YahooField.ADJCLOSE))
            .filter(node -> !isMissing(node))
            .orElse(null);
    }

    /**
     * Retrieves the adjusted close value at the given index, or {@code null} when missing.
     *
     * @param adjClose the adjclose array node.
     * @param i        the index of the value to retrieve.
     * @return the adjusted close at the index, or {@code null} when the array is missing/null at that index.
     */
    private BigDecimal adjCloseAt(JsonNode adjClose, int i) {
        return adjClose == null ? null : getDecimal(adjClose.path(YahooField.ADJCLOSE), i);
    }

    /**
     * Builds a YahooDividend object from the given JSON node by extracting the date and amount of the dividend.
     *
     * @param node the JsonNode representing an individual dividend event to be parsed.
     * @return a YahooDividend object containing the date and amount of the dividend extracted from the JSON node.
     * @throws ParseException if there is an error during parsing of the dividend data from the JSON node.
     */
    private YahooDividend buildYahooDividend(JsonNode node) throws ParseException {
        return new YahooDividend(
            Instant.ofEpochSecond(getRequiredLong(node, YahooField.DATE)),
            getRequiredDecimal(node, YahooField.AMOUNT)
        );
    }

    /**
     * Builds a YahooSplit object from the given JSON node by extracting the date and split ratio of the split event.
     *
     * @param node the JsonNode representing an individual split event to be parsed.
     * @return a YahooSplit object containing the date and split ratio of the split event extracted from the JSON node.
     * @throws ParseException if there is an error during parsing of the split data from the JSON node.
     */
    private YahooSplit buildYahooSplit(JsonNode node) throws ParseException {
        return new YahooSplit(
            Instant.ofEpochSecond(getRequiredLong(node, YahooField.DATE)),
            getRequiredText(node, YahooField.SPLIT_RATIO)
        );
    }

    /**
     * Retrieves the first result node from the given JSON root node, which contains the chart data.
     *
     * @param root the JsonNode representing the root of the JSON response to be parsed.
     * @return an Optional containing the first result JsonNode if it exists and is valid, or an empty Optional if no
     * valid result is found.
     */
    private Optional<JsonNode> firstResult(JsonNode root) {
        JsonNode result = root.path(YahooField.CHART).path(YahooField.RESULT);

        return firstElement(result)
            .filter(node -> !isMissing(node));
    }

    /**
     * Extracts the stock symbol from the given JSON result node by retrieving the required text value from the "meta" field.
     *
     * @param result the JsonNode containing the chart data from which to extract the stock symbol.
     * @return the stock symbol extracted from the JSON result node.
     * @throws ParseException if there is an error during parsing of the stock symbol from the JSON result node.
     */
    private String extractSymbol(JsonNode result) {
        return getRequiredText(result.path(YahooField.META), YahooField.SYMBOL);
    }

    /**
     * A functional interface representing a method that extracts a ChartAggregate from a given stock symbol and JSON result.
     * This interface is used to define different extraction methods for historical data, dividends, and splits based on the
     * specified YahooEvent type.
     */
    @FunctionalInterface
    private interface EventExtractor {
        ChartAggregate extract(String symbol, JsonNode result) throws ParseException;
    }

    /**
     * A functional interface representing a method that maps a JsonNode to an instance of type T, which can be used to
     * extract specific event data (e.g., dividends or splits) from a JSON node. This interface is used in the buildEventList
     * method to define how individual event nodes should be parsed into their corresponding objects.
     *
     * @param <T> the type of object that the JsonNode will be mapped to (e.g., YahooDividend or YahooSplit).
     */
    @FunctionalInterface
    private interface EventNodeMapper<T> {
        T map(JsonNode node) throws ParseException;
    }
}

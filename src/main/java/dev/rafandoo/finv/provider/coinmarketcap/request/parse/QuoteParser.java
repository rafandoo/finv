package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * Parses {@code /quotes/latest} responses into a {@link CmcQuote}.
 */
@Slf4j
public final class QuoteParser extends BaseParser implements ResponseParser<CmcQuote> {

    private final String symbol;
    private final String convert;

    public QuoteParser(String symbol, String convert) {
        this.symbol = symbol;
        this.convert = convert;
    }

    @Override
    public Optional<CmcQuote> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);
        JsonNode data = root.path("data");
        JsonNode entry = data.path(symbol);
        if (entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }
        JsonNode quote = entry.path("quote").path(convert);
        if (quote.isMissingNode() || quote.isNull()) {
            return Optional.empty();
        }

        BigDecimal price = bigDecimal(quote, "price").orElse(null);
        BigDecimal changePercent = bigDecimal(quote, "percent_change_24h").orElse(null);
        BigDecimal volume24h = bigDecimal(quote, "volume_24h").orElse(null);
        Instant lastUpdated = parseInstant(quote.path("last_updated").asText(null));

        return Optional.of(new CmcQuote(symbol, price, volume24h, changePercent, lastUpdated, convert));
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception e) {
            log.warn("#{} - could not parse last_updated '{}'", "coinmarketcap", value, e);
            return null;
        }
    }
}

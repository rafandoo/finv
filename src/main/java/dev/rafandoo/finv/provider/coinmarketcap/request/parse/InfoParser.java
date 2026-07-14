package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Parses {@code /info} responses into a {@link CmcInfo}.
 */
@Slf4j
public final class InfoParser extends BaseParser implements ResponseParser<CmcInfo> {

    private final String symbol;

    public InfoParser(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public Optional<CmcInfo> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);
        JsonNode data = root.path("data");
        JsonNode entry = data.path(symbol);
        if (entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }
        String name = textOrNull(entry, "name");
        String slug = textOrNull(entry, "symbol");
        String description = textOrNull(entry, "description");
        String category = textOrNull(entry, "category");
        return Optional.of(new CmcInfo(slug != null ? slug : symbol, name, description, category));
    }
}

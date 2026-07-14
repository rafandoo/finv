package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Shared base for CoinMarketCap parsers: Jackson tree read + CMC error-code gate.
 */
@Slf4j
public abstract class BaseParser {

    protected static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = JacksonMapperFactory.json();

    /**
     * Reads the response body into a JSON tree and throws if CMC reports an API error.
     *
     * @param response the HTTP response.
     * @return the parsed root node.
     * @throws ParseException on read failure or non-zero {@code status.error_code}.
     */
    protected JsonNode readRoot(HttpResponse response) throws ParseException {
        try (response) {
            JsonNode root = MAPPER.readTree(response.getBodyAsString());

            JsonNode status = root.path("status");
            int errorCode = status.path("error_code").asInt(0);
            if (errorCode != 0) {
                String message = status.path("error_message").asText("unknown error");
                throw new ParseException(
                    "#%s - CMC API error #%d: %s".formatted(
                        CoinMarketCapMetadata.METADATA.id(), errorCode, message)
                );
            }
            return root;
        } catch (IOException e) {
            throw new ParseException("Failed to read CoinMarketCap response", e);
        }
    }

    protected java.util.Optional<BigDecimal> bigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull()
            ? java.util.Optional.empty()
            : java.util.Optional.of(value.decimalValue());
    }

    protected String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }
}

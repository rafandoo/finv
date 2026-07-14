package dev.rafandoo.finv.provider.yahoo.request.parse;

import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteData;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteParserTest {

    private static final String VALID_QUOTE = """
        {
          "quoteResponse": {
            "result": [
              {
                "symbol": "AAPL",
                "shortName": "Apple Inc.",
                "longName": "Apple Inc.",
                "quoteType": "EQUITY",
                "fullExchangeName": "NASDAQ",
                "currency": "USD"
              }
            ],
            "error": null
          }
        }
        """;

    private static final String MISSING_SYMBOL = """
        {
          "quoteResponse": {
            "result": [
              {
                "shortName": "Apple Inc.",
                "quoteType": "EQUITY",
                "currency": "USD"
              }
            ],
            "error": null
          }
        }
        """;

    private static final String EMPTY_RESULT = """
        {
          "quoteResponse": {
            "result": [],
            "error": null
          }
        }
        """;

    private static final String MISSING_RESULT = """
        {
          "quoteResponse": {
            "error": null
          }
        }
        """;

    @Test
    void shouldParseValidQuote() throws Exception {
        HttpResponse response = HttpResponseFixture.json(VALID_QUOTE);
        Optional<QuoteData> parsed = new QuoteParser().parse(response);

        assertThat(parsed).isPresent();
        QuoteData data = parsed.get();
        assertThat(data.symbol()).isEqualTo("AAPL");
        assertThat(data.shortName()).isEqualTo("Apple Inc.");
        assertThat(data.longName()).isEqualTo("Apple Inc.");
        assertThat(data.assetType()).isEqualTo(AssetType.STOCK);
        assertThat(data.exchangeName()).isEqualTo("NASDAQ");
        assertThat(data.currency().getCurrencyCode()).isEqualTo("USD");
    }

    @Test
    void shouldMapEtfQuoteTypeWhenQuoteTypeIsEtf() throws Exception {
        String json = VALID_QUOTE.replace("\"EQUITY\"", "\"ETF\"");
        HttpResponse response = HttpResponseFixture.json(json);
        Optional<QuoteData> parsed = new QuoteParser().parse(response);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().assetType()).isEqualTo(AssetType.ETF);
    }

    @Test
    void shouldThrowWhenRequiredSymbolMissing() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MISSING_SYMBOL);
        assertThatThrownBy(() -> new QuoteParser().parse(response))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldThrowWhenResultArrayEmpty() throws Exception {
        HttpResponse response = HttpResponseFixture.json(EMPTY_RESULT);
        assertThatThrownBy(() -> new QuoteParser().parse(response))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldReturnEmptyWhenResultMissing() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MISSING_RESULT);
        Optional<QuoteData> parsed = new QuoteParser().parse(response);
        assertThat(parsed).isEmpty();
    }
}

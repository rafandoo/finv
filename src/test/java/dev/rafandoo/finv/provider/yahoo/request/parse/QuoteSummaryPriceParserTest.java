package dev.rafandoo.finv.provider.yahoo.request.parse;

import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteSummaryPrice;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteSummaryPriceParserTest {

    private static final String FULL_PRICE = """
        {
          "quoteSummary": {
            "result": [
              {
                "price": {
                  "symbol": "AAPL",
                  "shortName": "Apple Inc.",
                  "currency": "USD",
                  "exchangeName": "NASDAQ",
                  "regularMarketPrice": 150.25,
                  "regularMarketDayHigh": 152.0,
                  "regularMarketDayLow": 149.0,
                  "regularMarketOpen": 150.0,
                  "regularMarketPreviousClose": 148.0,
                  "regularMarketVolume": 123456,
                  "regularMarketChange": 2.25,
                  "regularMarketChangePercent": 1.5,
                  "regularMarketTime": 1609459200
                }
              }
            ],
            "error": null
          }
        }
        """;

    private static final String MINIMAL_PRICE = """
        {
          "quoteSummary": {
            "result": [
              { "price": { "symbol": "AAPL" } }
            ],
            "error": null
          }
        }
        """;

    private static final String MISSING_PRICE = """
        {
          "quoteSummary": {
            "result": [ {} ],
            "error": null
          }
        }
        """;

    private static final String MISSING_SYMBOL = """
        {
          "quoteSummary": {
            "result": [
              { "price": { "shortName": "Apple Inc." } }
            ],
            "error": null
          }
        }
        """;

    @Test
    void shouldParseFullPrice() throws Exception {
        HttpResponse response = HttpResponseFixture.json(FULL_PRICE);
        Optional<QuoteSummaryPrice> parsed = new QuoteSummaryPriceParser().parse(response);

        assertThat(parsed).isPresent();
        QuoteSummaryPrice price = parsed.get();
        assertThat(price.symbol()).isEqualTo("AAPL");
        assertThat(price.name()).isEqualTo("Apple Inc.");
        assertThat(price.currency()).isEqualTo("USD");
        assertThat(price.exchange()).isEqualTo("NASDAQ");
        assertThat(price.price()).isEqualTo(new BigDecimal("150.25"));
        assertThat(price.high()).isEqualTo(new BigDecimal("152.0"));
        assertThat(price.volume()).isEqualTo(BigInteger.valueOf(123456));
        assertThat(price.marketTime()).isNotNull();
    }

    @Test
    void shouldParseMinimalPriceWhenOptionalFieldsAbsent() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MINIMAL_PRICE);
        Optional<QuoteSummaryPrice> parsed = new QuoteSummaryPriceParser().parse(response);

        assertThat(parsed).isPresent();
        QuoteSummaryPrice price = parsed.get();
        assertThat(price.symbol()).isEqualTo("AAPL");
        assertThat(price.price()).isNull();
        assertThat(price.currency()).isNull();
    }

    @Test
    void shouldReturnEmptyWhenPriceNodeMissing() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MISSING_PRICE);
        Optional<QuoteSummaryPrice> parsed = new QuoteSummaryPriceParser().parse(response);
        assertThat(parsed).isEmpty();
    }

    @Test
    void shouldThrowWhenSymbolMissing() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MISSING_SYMBOL);
        assertThatThrownBy(() -> new QuoteSummaryPriceParser().parse(response))
            .isInstanceOf(ParseException.class);
    }
}

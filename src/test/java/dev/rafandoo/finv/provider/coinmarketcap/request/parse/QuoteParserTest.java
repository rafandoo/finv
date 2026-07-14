package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteParserTest {

    private static final String OK = """
        {
          "status": { "error_code": 0, "error_message": null },
          "data": {
            "BTC": {
              "id": 1,
              "name": "Bitcoin",
              "quote": {
                "USD": {
                  "price": 50000.0,
                  "volume_24h": 30000000000.0,
                  "percent_change_24h": 2.5,
                  "last_updated": "2024-01-01T12:00:00.000Z"
                }
              }
            }
          }
        }
        """;

    private static final String EMPTY = """
        {
          "status": { "error_code": 0, "error_message": null },
          "data": { }
        }
        """;

    private static final String API_ERROR = """
        {
          "status": { "error_code": 400, "error_message": "symbol invalid" },
          "data": null
        }
        """;

    @Test
    void parsesValidQuote() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        Optional<CmcQuote> result = parser.parse(HttpResponseFixture.json(OK));

        assertThat(result).isPresent();
        CmcQuote q = result.get();
        assertThat(q.symbol()).isEqualTo("BTC");
        assertThat(q.price()).isEqualByComparingTo("50000.0");
        assertThat(q.changePercent()).isEqualByComparingTo("2.5");
        assertThat(q.convert()).isEqualTo("USD");
        assertThat(q.lastUpdated()).isNotNull();
    }

    @Test
    void emptyDataReturnsEmpty() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        assertThat(parser.parse(HttpResponseFixture.json(EMPTY))).isEmpty();
    }

    @Test
    void apiErrorThrows() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        assertThatThrownBy(() -> parser.parse(HttpResponseFixture.json(API_ERROR)))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("symbol invalid");
    }
}

package dev.rafandoo.finv.provider.yahoo.request.parse;

import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.model.ChartAggregate;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooCandle;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooDividend;
import dev.rafandoo.finv.provider.yahoo.request.model.YahooSplit;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinanceChartParserTest {

    private static final String HISTORY = """
        {
          "chart": {
            "result": [
              {
                "meta": { "symbol": "AAPL" },
                "timestamp": [1609459200, 1609545600],
                "indicators": {
                  "quote": [
                    {
                      "open": [130.0, 131.0],
                      "high": [132.0, 133.0],
                      "low": [129.0, 128.0],
                      "close": [131.5, 132.5],
                      "volume": [100, 200]
                    }
                  ]
                }
              }
            ],
            "error": null
          }
        }
        """;

    private static final String HISTORY_WITH_ADJCLOSE = """
        {
          "chart": {
            "result": [
              {
                "meta": { "symbol": "AAPL" },
                "timestamp": [1609459200],
                "indicators": {
                  "quote": [
                    { "open": [130.0], "high": [132.0], "low": [129.0], "close": [131.5], "volume": [100] }
                  ],
                  "adjclose": [
                    { "adjclose": [130.5] }
                  ]
                }
              }
            ],
            "error": null
          }
        }
        """;

    private static final String DIVIDENDS = """
        {
          "chart": {
            "result": [
              {
                "meta": { "symbol": "AAPL" },
                "events": {
                  "dividends": {
                    "2021-02-05": { "date": 1612483200, "amount": 0.205 }
                  }
                }
              }
            ],
            "error": null
          }
        }
        """;

    private static final String SPLITS = """
        {
          "chart": {
            "result": [
              {
                "meta": { "symbol": "AAPL" },
                "events": {
                  "splits": {
                    "2020-08-31": { "date": 1598832000, "numerator": 4, "denominator": 1, "splitRatio": "4:1" }
                  }
                }
              }
            ],
            "error": null
          }
        }
        """;

    private static final String EMPTY_RESULT = """
        {
          "chart": {
            "result": [],
            "error": null
          }
        }
        """;

    private static final String MISSING_RESULT = """
        {
          "chart": {
            "error": null
          }
        }
        """;

    @Test
    void shouldParseHistory() throws Exception {
        HttpResponse response = HttpResponseFixture.json(HISTORY);
        Optional<ChartAggregate> parsed = new FinanceChartParser(YahooEvent.HISTORY).parse(response);

        assertThat(parsed).isPresent();
        ChartAggregate agg = parsed.get();
        assertThat(agg.symbol()).isEqualTo("AAPL");
        assertThat(agg.candles()).hasSize(2);
        YahooCandle first = agg.candles().getFirst();
        assertThat(first.open()).isEqualTo(new BigDecimal("130.0"));
        assertThat(first.high()).isEqualTo(new BigDecimal("132.0"));
        assertThat(first.close()).isEqualTo(new BigDecimal("131.5"));
        assertThat(first.volume()).isEqualTo(BigInteger.valueOf(100));
        assertThat(agg.dividends()).isEmpty();
        assertThat(agg.splits()).isEmpty();
    }

    @Test
    void shouldParseHistoryWhenAdjCloseIncluded() throws Exception {
        HttpResponse response = HttpResponseFixture.json(HISTORY_WITH_ADJCLOSE);
        Optional<ChartAggregate> parsed =
            new FinanceChartParser(YahooEvent.HISTORY, true).parse(response);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().candles().getFirst().adjClose()).isEqualTo(new BigDecimal("130.5"));
    }

    @Test
    void shouldOmitAdjCloseWhenNotRequested() throws Exception {
        HttpResponse response = HttpResponseFixture.json(HISTORY_WITH_ADJCLOSE);
        Optional<ChartAggregate> parsed =
            new FinanceChartParser(YahooEvent.HISTORY, false).parse(response);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().candles().getFirst().adjClose()).isNull();
    }

    @Test
    void shouldParseDividends() throws Exception {
        HttpResponse response = HttpResponseFixture.json(DIVIDENDS);
        Optional<ChartAggregate> parsed = new FinanceChartParser(YahooEvent.DIVIDEND).parse(response);

        assertThat(parsed).isPresent();
        ChartAggregate agg = parsed.get();
        assertThat(agg.dividends()).hasSize(1);
        YahooDividend dividend = agg.dividends().getFirst();
        assertThat(dividend.amount()).isEqualTo(new BigDecimal("0.205"));
        assertThat(agg.candles()).isEmpty();
    }

    @Test
    void shouldParseSplits() throws Exception {
        HttpResponse response = HttpResponseFixture.json(SPLITS);
        Optional<ChartAggregate> parsed = new FinanceChartParser(YahooEvent.SPLIT).parse(response);

        assertThat(parsed).isPresent();
        ChartAggregate agg = parsed.get();
        assertThat(agg.splits()).hasSize(1);
        YahooSplit split = agg.splits().getFirst();
        assertThat(split.splitRatio()).isEqualTo("4:1");
    }

    @Test
    void shouldThrowWhenResultArrayEmpty() throws Exception {
        HttpResponse response = HttpResponseFixture.json(EMPTY_RESULT);
        assertThatThrownBy(() -> new FinanceChartParser(YahooEvent.HISTORY).parse(response))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldReturnEmptyWhenResultMissing() throws Exception {
        HttpResponse response = HttpResponseFixture.json(MISSING_RESULT);
        Optional<ChartAggregate> parsed = new FinanceChartParser(YahooEvent.HISTORY).parse(response);
        assertThat(parsed).isEmpty();
    }
}

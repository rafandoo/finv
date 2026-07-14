package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.finv.core.market.Interval;
import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.model.ChartAggregate;
import dev.rafandoo.finv.provider.yahoo.request.parse.FinanceChartParser;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FinanceChartRequestTest {

    private static final TimeRange RANGE = new TimeRange(
        Instant.parse("2023-01-01T00:00:00Z"),
        Instant.parse("2023-01-31T23:59:59Z"),
        Interval.DAILY
    );

    @Test
    void shouldTargetHistoryByDefault() {
        FinanceChartRequest request = new FinanceChartRequest("AAPL", RANGE);
        String url = request.url().toString();
        assertThat(url).contains("AAPL").contains("chart").contains("interval=1d");
        ResponseParser<ChartAggregate> parser = request.parser();
        assertThat(parser).isInstanceOf(FinanceChartParser.class);
    }

    @Test
    void shouldTargetDividendsWhenDividendEvent() {
        FinanceChartRequest request = new FinanceChartRequest("AAPL", RANGE, YahooEvent.DIVIDEND);
        String url = request.url().toString();
        assertThat(url).contains("events=div");
    }

    @Test
    void shouldSetIncludeAdjCloseFlag() {
        FinanceChartRequest request = new FinanceChartRequest("AAPL", RANGE, true);
        String url = request.url().toString();
        assertThat(url).contains("includeAdjustedClose=true");
    }

    @Test
    void shouldRequireCrumbByDefault() {
        assertThat(new FinanceChartRequest("AAPL", RANGE).requiresCrumb()).isTrue();
    }
}

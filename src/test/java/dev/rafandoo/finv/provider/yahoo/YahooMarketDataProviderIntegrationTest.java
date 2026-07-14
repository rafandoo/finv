package dev.rafandoo.finv.provider.yahoo;

import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hits the live Yahoo Finance endpoint. This is excluded from the default {@code test} task (the
 * {@code integration} tag is filtered out in {@code build.gradle}) and must be run explicitly:
 *
 * <pre>
 * ./gradlew test --tests "*IntegrationTest"
 * </pre>
 *
 * It will fail when there is no network access or Yahoo is unavailable, by design.
 */
@Tag("integration")
class YahooMarketDataProviderIntegrationTest {

    private static final Stock AAPL = new Stock("AAPL", "NASDAQ", Currency.getInstance("USD"));

    private static TimeRange lastYear() {
        Instant end = Instant.now();
        return TimeRange.of(end.minusSeconds(365L * 24 * 60 * 60), end);
    }

    private final YahooMarketDataProvider provider = new YahooMarketDataProvider();

    @Test
    void shouldFetchLatestQuoteFromYahoo() {
        Optional<Quote> quote = provider.latest(AAPL);
        assertThat(quote).isPresent();
        assertThat(quote.get().symbol()).isEqualTo("AAPL");
        assertThat(quote.get().price()).isNotNull();
    }

    @Test
    void shouldFetchHistoryFromYahoo() {
        List<Candle> candles = provider.history(AAPL, lastYear()).toList();
        assertThat(candles).isNotEmpty();
        assertThat(candles).allSatisfy(c -> assertThat(c.open()).isNotNull());
    }

    @Test
    void shouldFetchDividendsFromYahoo() {
        List<DividendEvent> dividends = provider.dividends(AAPL, lastYear()).toList();
        assertThat(dividends).isNotNull();
    }

    @Test
    void shouldFetchSplitsFromYahoo() {
        List<SplitEvent> splits = provider.splits(AAPL, lastYear()).toList();
        assertThat(splits).isNotNull();
    }
}

package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Crypto;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapClient;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoinMarketCapMarketDataProviderTest {

    private final CoinMarketCapClient client = mock(CoinMarketCapClient.class);
    private final CoinMarketCapMarketDataProvider provider =
        new CoinMarketCapMarketDataProvider(client);

    private final Crypto btc = new Crypto("BTC", "USDT", "Binance", Currency.getInstance("USD"));

    @Test
    void supportsCrypto() {
        assertThat(provider.supports(AssetType.CRYPTO)).isTrue();
        assertThat(provider.supports(AssetType.STOCK)).isFalse();
        assertThat(provider.metadata().id()).isEqualTo("coinmarketcap");
    }

    @Test
    void latestReturnsQuote() {
        CmcQuote cmc = new CmcQuote("BTC", new BigDecimal("50000"),
            new BigDecimal("3e10"), new BigDecimal("2.5"),
            Instant.parse("2024-01-01T12:00:00Z"), "USD");
        when(client.execute(any())).thenReturn(Optional.of(cmc));

        Optional<Quote> quote = provider.latest(btc);
        assertThat(quote).isPresent();
        assertThat(quote.get().symbol()).isEqualTo("BTC/USDT");
        assertThat(quote.get().price()).isEqualByComparingTo("50000");
        assertThat(quote.get().changePercent()).isEqualByComparingTo("2.5");
        // absolute change derived from percent: change = price - price/(1+pct/100)
        // 50000 / 1.025 = 48780.48780487805; change = 1219.51219512195
        assertThat(quote.get().change()).isEqualByComparingTo("1219.51219512195");
    }

    @Test
    void instrumentReturnsMetadata() {
        CmcInfo info = new CmcInfo("BTC", "Bitcoin", "desc", "coin");
        when(client.execute(any())).thenReturn(Optional.of(info));

        Optional<Instrument> instrument = provider.instrument(btc);
        assertThat(instrument).isPresent();
        assertThat(instrument.get().symbol()).isEqualTo("BTC/USDT");
        assertThat(instrument.get().name()).isEqualTo("Bitcoin");
        assertThat(instrument.get().assetType()).isEqualTo(AssetType.CRYPTO);
        assertThat(instrument.get().exchange()).isEqualTo("Binance");
        assertThat(instrument.get().currency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    void defaultsMissingCurrencyToUsd() {
        Crypto noCurrency = new Crypto("BTC", "USDT", "Binance", null);
        CmcQuote cmc = new CmcQuote("BTC", new BigDecimal("50000"), null,
            new BigDecimal("1.0"), Instant.parse("2024-01-01T12:00:00Z"), "USD");
        when(client.execute(any())).thenReturn(Optional.of(cmc));

        assertThat(provider.latest(noCurrency)).isPresent();
    }

    @Test
    void unsupportedOperationsThrow() {
        assertThatThrownBy(() -> provider.history(btc, null))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> provider.history(btc, null, false))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> provider.dividends(null, null))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> provider.splits(null, null))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}

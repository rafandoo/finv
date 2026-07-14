package dev.rafandoo.finv.provider;

import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProviderRegistryTest {

    private static final Stock AAPL = new Stock("AAPL", "NASDAQ", Currency.getInstance("USD"));

    private static MarketDataProvider stub(AssetType type, Feature... features) {
        return new MarketDataProvider() {
            @Override
            public ProviderMetadata metadata() {
                return new ProviderMetadata("stub", "Stub", Set.of(type), Set.of(features));
            }

            @Override
            public boolean supports(AssetType t) {
                return t == type;
            }

            @Override
            public Optional<Instrument> instrument(Asset asset) {
                return Optional.empty();
            }

            @Override
            public Optional<Quote> latest(Asset asset) {
                return Optional.empty();
            }

            @Override
            public Stream<Candle> history(Asset asset, TimeRange range) {
                return Stream.empty();
            }

            @Override
            public Stream<Candle> history(Asset asset, TimeRange range, boolean includeAdjClose) {
                return Stream.empty();
            }

            @Override
            public Stream<DividendEvent> dividends(Stock stock, TimeRange range) {
                return Stream.empty();
            }

            @Override
            public Stream<SplitEvent> splits(Stock stock, TimeRange range) {
                return Stream.empty();
            }
        };
    }

    @Test
    void shouldResolveRegisteredProviderByAssetTypeAndFeature() {
        ProviderRegistry registry = new ProviderRegistry();
        MarketDataProvider provider = stub(AssetType.STOCK, Feature.REALTIME);
        registry.register(provider);

        MarketDataProvider resolved = registry.resolve(AAPL, Feature.REALTIME);

        assertThat(resolved).isSameAs(provider);
    }

    @Test
    void shouldThrowWhenNoProviderSupportsAssetType() {
        ProviderRegistry registry = new ProviderRegistry();
        registry.register(stub(AssetType.CRYPTO, Feature.REALTIME));

        assertThatThrownBy(() -> registry.resolve(AAPL, Feature.REALTIME))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("No provider found");
    }

    @Test
    void shouldThrowWhenProviderLacksFeature() {
        ProviderRegistry registry = new ProviderRegistry();
        registry.register(stub(AssetType.STOCK, Feature.REALTIME));

        assertThatThrownBy(() -> registry.resolve(AAPL, Feature.HISTORY))
            .isInstanceOf(RuntimeException.class);
    }
}

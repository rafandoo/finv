package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Crypto;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapClient;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.InfoRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.LatestQuoteRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

/**
 * CoinMarketCap keyless implementation of {@link MarketDataProvider} for crypto.
 *
 * <p>Supports {@code latest()} (real-time quote) and {@code instrument()} (metadata) only.
 * Historical/splits/dividends are not available in the keyless API.</p>
 */
@Slf4j
public final class CoinMarketCapMarketDataProvider implements MarketDataProvider {

    private final CoinMarketCapClient client;

    public CoinMarketCapMarketDataProvider() {
        this(new CoinMarketCapClient());
    }

    public CoinMarketCapMarketDataProvider(CoinMarketCapClient client) {
        this.client = client;
    }

    private static Currency convertOf(Crypto crypto) {
        Currency c = crypto.currency();
        return (c == null || c.getCurrencyCode().isBlank()) ? Currency.getInstance("USD") : c;
    }

    @Override
    public ProviderMetadata metadata() {
        return CoinMarketCapMetadata.METADATA;
    }

    @Override
    public boolean supports(AssetType type) {
        return CoinMarketCapMetadata.METADATA.supportedAssetTypes().contains(type);
    }

    @Override
    public Optional<Instrument> instrument(Asset asset) {
        Crypto crypto = asCrypto(asset);
        return client.execute(new InfoRequest(crypto.base()))
            .map(info -> new Instrument(
                asset.symbol(),
                info.name() != null ? info.name() : crypto.base(),
                info.description(),
                AssetType.CRYPTO,
                crypto.exchange(),
                currencyOf(crypto)
            ));
    }

    @Override
    public Optional<Quote> latest(Asset asset) {
        Crypto crypto = asCrypto(asset);
        Currency convert = convertOf(crypto);
        return client.execute(new LatestQuoteRequest(crypto.base(), convert.getCurrencyCode()))
            .map(q -> q.toQuote(asset.symbol()));
    }

    @Override
    public java.util.stream.Stream<Candle> history(Asset asset, TimeRange range) {
        throw new UnsupportedOperationException("CoinMarketCap keyless API does not provide historical data");
    }

    @Override
    public java.util.stream.Stream<Candle> history(Asset asset, TimeRange range, boolean includeAdjClose) {
        throw new UnsupportedOperationException("CoinMarketCap keyless API does not provide historical data");
    }

    @Override
    public java.util.stream.Stream<DividendEvent> dividends(Stock stock, TimeRange range) {
        throw new UnsupportedOperationException("Dividends are not applicable to crypto");
    }

    @Override
    public java.util.stream.Stream<SplitEvent> splits(Stock stock, TimeRange range) {
        throw new UnsupportedOperationException("Splits are not applicable to crypto");
    }

    private static Crypto asCrypto(Asset asset) {
        if (!(asset instanceof Crypto crypto)) {
            throw new IllegalArgumentException(
                "CoinMarketCap provider only supports Crypto assets, got: " + asset.type());
        }
        return crypto;
    }

    private static Currency currencyOf(Crypto crypto) {
        Currency c = crypto.currency();
        return c != null ? c : Currency.getInstance("USD");
    }
}

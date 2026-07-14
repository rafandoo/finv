package dev.rafandoo.finv.provider.yahoo;

import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import dev.rafandoo.finv.core.exception.ProviderRequestException;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.provider.yahoo.request.YahooFinanceClient;
import dev.rafandoo.finv.provider.yahoo.request.auth.AuthManager;
import dev.rafandoo.finv.provider.yahoo.request.auth.AuthState;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.YahooRequest;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.FinanceChartRequest;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.QuoteRequest;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.QuoteSummaryPriceRequest;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Yahoo Finance implementation of {@link MarketDataProvider}.
 *
 * <p>All network calls are routed through {@link #executeWithRetry(YahooRequest)} so that a stale
 * crumb (Yahoo's request token) is transparently refreshed and retried a single time.</p>
 */
@Slf4j
public final class YahooMarketDataProvider implements MarketDataProvider {

    private YahooFinanceClient getClient() {
        return new YahooFinanceClient(AuthManager.getAuth());
    }

    /**
     * Executes a request, transparently re-authenticating once when Yahoo rejects the current crumb
     * (e.g. it expired between {@link AuthManager#getAuth()} and the network call). The retry uses a
     * freshly obtained {@link AuthState} so a stale-crumb failure does not surface as an error.
     *
     * @param request the request to execute.
     * @param <T>     the response type.
     * @return the parsed response.
     * @throws ProviderRequestException when the request fails for any reason other than a recoverable auth error.
     */
    private <T> Optional<T> executeWithRetry(YahooRequest<T> request) {
        try {
            return this.getClient().execute(request);
        } catch (ProviderRequestException e) {
            if (e.isAuthFailure()) {
                log.warn(
                    "#{} - Crumb rejected, refreshing authentication and retrying",
                    YahooMetadata.METADATA.id()
                );
                AuthManager.refresh();
                return new YahooFinanceClient(AuthManager.getAuth()).execute(request);
            }
            throw e;
        }
    }

    @Override
    public ProviderMetadata metadata() {
        return YahooMetadata.METADATA;
    }

    @Override
    public boolean supports(AssetType type) {
        return this.metadata().supportedAssetTypes().contains(type);
    }

    @Override
    public Optional<Instrument> instrument(Asset asset) {
        return this.executeWithRetry(new QuoteRequest(asset.symbol()))
            .map(QuoteData::toInstrument);
    }

    @Override
    public Optional<Quote> latest(Asset asset) {
        return this.executeWithRetry(new QuoteSummaryPriceRequest(asset.symbol()))
            .map(QuoteSummaryPrice::toQuote);
    }

    @Override
    public Stream<Candle> history(Asset asset, TimeRange range) {
        return this.history(asset, range, false);
    }

    @Override
    public Stream<Candle> history(Asset asset, TimeRange range, boolean includeAdjClose) {
        return this.executeWithRetry(new FinanceChartRequest(asset.symbol(), range, includeAdjClose))
            .stream()
            .map(ChartAggregate::candles)
            .flatMap(List::stream)
            .map(YahooCandle::toCandle);
    }

    @Override
    public Stream<DividendEvent> dividends(Stock stock, TimeRange range) {
        return this.executeWithRetry(new FinanceChartRequest(stock.symbol(), range, YahooEvent.DIVIDEND))
            .stream()
            .map(ChartAggregate::dividends)
            .flatMap(List::stream)
            .map(YahooDividend::toDividendEvent);
    }

    @Override
    public Stream<SplitEvent> splits(Stock stock, TimeRange range) {
        return this.executeWithRetry(new FinanceChartRequest(stock.symbol(), range, YahooEvent.SPLIT))
            .stream()
            .map(ChartAggregate::splits)
            .flatMap(List::stream)
            .map(YahooSplit::toSplitEvent);
    }
}

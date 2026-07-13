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
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.provider.yahoo.request.YahooFinanceClient;
import dev.rafandoo.finv.provider.yahoo.request.auth.AuthManager;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.FinanceChartRequest;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.QuoteRequest;
import dev.rafandoo.finv.provider.yahoo.request.endpoint.QuoteSummaryPriceRequest;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class YahooMarketDataProvider implements MarketDataProvider {

    private YahooFinanceClient getClient() {
        return new YahooFinanceClient(AuthManager.getAuth());
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
        return this.getClient()
            .execute(new QuoteRequest(asset.symbol()))
            .map(QuoteData::toInstrument);
    }

    @Override
    public Optional<Quote> latest(Asset asset) {
        return this.getClient()
            .execute(new QuoteSummaryPriceRequest(asset.symbol()))
            .map(QuoteSummaryPrice::toQuote);
    }

    @Override
    public Stream<Candle> history(Asset asset, TimeRange range) {
        return this.getClient()
            .execute(new FinanceChartRequest(asset.symbol(), range))
            .stream()
            .map(ChartAggregate::candles)
            .flatMap(List::stream)
            .map(YahooCandle::toCandle);
    }

    @Override
    public Optional<Stream<DividendEvent>> dividends(Stock stock, TimeRange range) {
        return Optional.ofNullable(
            this.getClient()
                .execute(new FinanceChartRequest(stock.symbol(), range, YahooEvent.DIVIDEND))
                .stream()
                .map(ChartAggregate::dividends)
                .flatMap(List::stream)
                .map(YahooDividend::toDividendEvent)
        );
    }

    @Override
    public Optional<Stream<SplitEvent>> splits(Stock stock, TimeRange range) {
        return Optional.ofNullable(
            this.getClient()
                .execute(new FinanceChartRequest(stock.symbol(), range, YahooEvent.SPLIT))
                .stream()
                .map(ChartAggregate::splits)
                .flatMap(List::stream)
                .map(YahooSplit::toSplitEvent)
        );
    }
}

package dev.rafandoo.finv.core.market.provider;

import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Quote;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface for market data providers. Implementations of this interface can provide
 * market data such as quotes, historical prices, dividends, and splits for various assets.
 */
public interface MarketDataProvider {

    /**
     * Returns metadata about the market data provider, such as its name and supported features.
     *
     * @return a MarketDataProviderMetadata object containing information about the provider.
     */
    ProviderMetadata metadata();

    /**
     * Checks if the provider supports a given asset type.
     *
     * @param type the asset type to check.
     * @return {@code true} if the provider supports the asset type, {@code false} otherwise.
     */
    boolean supports(AssetType type);

    /**
     * Retrieves the instrument information for a given asset.
     *
     * @param asset the asset for which to retrieve the instrument information.
     * @return an Optional containing the Instrument if found, or an empty Optional if not found.
     */
    Optional<Instrument> instrument(Asset asset);

    /**
     * Retrieves the latest quote for a given asset.
     *
     * @param asset the asset for which to retrieve the latest quote.
     * @return an Optional containing the Quote if found, or an empty Optional if not found.
     */
    Optional<Quote> latest(Asset asset);

    /**
     * Retrieves the historical price data (candles) for a given asset and time range.
     *
     * @param asset the asset for which to retrieve historical data.
     * @param range the time range for which to retrieve historical data.
     * @return a Stream of Candle objects representing the historical price data.
     */
    Stream<Candle> history(Asset asset, TimeRange range);

    /**
     * Retrieves the dividend events for a given stock and time range.
     *
     * @param stock the stock for which to retrieve dividend events.
     * @param range the time range for which to retrieve dividend events.
     * @return an Optional containing a Stream of DividendEvent objects if found, or an empty Optional if not found.
     */
    Optional<Stream<DividendEvent>> dividends(Stock stock, TimeRange range);

    /**
     * Retrieves the split events for a given stock and time range.
     *
     * @param stock the stock for which to retrieve split events.
     * @param range the time range for which to retrieve split events.
     * @return an Optional containing a Stream of SplitEvent objects if found, or an empty Optional if not found.
     */
    Optional<Stream<SplitEvent>> splits(Stock stock, TimeRange range);

}

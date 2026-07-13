package dev.rafandoo.finv.provider.yahoo.request.model;

import java.util.List;

/**
 * Represents the aggregated data for a financial instrument as returned by the Yahoo Finance API.
 *
 * @param symbol    the ticker symbol of the financial instrument.
 * @param candles   a list of historical price data (candles) for the instrument.
 * @param dividends a list of dividend events for the instrument.
 * @param splits    a list of stock split events for the instrument.
 */
public record ChartAggregate(
    String symbol,
    List<YahooCandle> candles,
    List<YahooDividend> dividends,
    List<YahooSplit> splits
) {
}

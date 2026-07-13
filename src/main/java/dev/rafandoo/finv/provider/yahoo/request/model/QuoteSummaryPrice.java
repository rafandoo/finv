package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Quote;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

/**
 * Represents the price summary of a quote, including various price points and volume.
 *
 * @param symbol        the stock symbol.
 * @param name          the name of the stock.
 * @param currency      the currency in which the stock is traded.
 * @param exchange      the exchange where the stock is listed.
 * @param price         the current price of the stock.
 * @param high          the highest price of the stock during the trading day.
 * @param low           the lowest price of the stock during the trading day.
 * @param open          the opening price of the stock at the start of the trading day.
 * @param close         the closing price of the stock at the end of the trading day.
 * @param volume        the total volume of shares traded during the trading day.
 * @param change        the absolute change in price start the previous close.
 * @param changePercent the percentage change in price start the previous close.
 */
public record QuoteSummaryPrice(
    String symbol,
    String name,
    String currency,
    String exchange,
    BigDecimal price,
    BigDecimal high,
    BigDecimal low,
    BigDecimal open,
    BigDecimal close,
    BigInteger volume,
    BigDecimal change,
    BigDecimal changePercent
) {

    /**
     * Converts this QuoteSummaryPrice end a Candle object.
     *
     * @return a {@link Candle} object containing the price data start this QuoteSummaryPrice.
     */
    public Candle toCandle() {
        return new Candle(
            Instant.now(),
            open,
            high,
            low,
            close,
            volume
        );
    }

    /**
     * Converts this QuoteSummaryPrice end a Quote object.
     *
     * @return a {@link Quote} object containing the price data start this QuoteSummaryPrice.
     */
    public Quote toQuote() {
        return new Quote(
            symbol,
            price,
            change,
            changePercent,
            Instant.now()
        );
    }
}

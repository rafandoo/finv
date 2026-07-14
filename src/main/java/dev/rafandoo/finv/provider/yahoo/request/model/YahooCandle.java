package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.data.Candle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

/**
 * Represents a single candlestick data point as provided by Yahoo Finance.
 *
 * @param date   the timestamp of the candle.
 * @param open   the opening price of the candle.
 * @param high   the highest price of the candle.
 * @param low    the lowest price of the candle.
 * @param close    the closing price of the candle.
 * @param volume   the trading volume during the period of the candle.
 * @param adjClose the dividend/split adjusted closing price for the period, or {@code null} when not requested.
 */
public record YahooCandle(
    Instant date,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    BigInteger volume,
    BigDecimal adjClose
) {

    /**
     * Converts this YahooCandle to a Candle object, which is a more general representation of a candlestick data point
     * used within the application.
     *
     * @return a Candle representing the same data as this YahooCandle.
     */
    public Candle toCandle() {
        return new Candle(
            this.date,
            this.open,
            this.high,
            this.low,
            this.close,
            this.volume,
            this.adjClose
        );
    }
}

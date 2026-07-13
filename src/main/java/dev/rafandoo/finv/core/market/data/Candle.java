package dev.rafandoo.finv.core.market.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

/**
 * Represents a single candlestick in a price chart, containing the open, high, low, close prices and volume for a
 * specific time period.
 *
 * @param date   the date and time of the candlestick.
 * @param open   the opening price of the candlestick.
 * @param high   the highest price reached during the candlestick period.
 * @param low    the lowest price reached during the candlestick period.
 * @param close  the closing price of the candlestick.
 * @param volume the trading volume during the candlestick period.
 */
public record Candle(
    Instant date,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    BigInteger volume
) {
}

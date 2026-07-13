package dev.rafandoo.finv.core.market.data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a quote for a financial instrument, containing the current price and related information.
 *
 * @param symbol        the symbol of the instrument.
 * @param price         the current price of the instrument.
 * @param change        the absolute change in price since the last quote.
 * @param changePercent the percentage change in price since the last quote.
 * @param date          the date and time when the quote was retrieved.
 */
public record Quote(
    String symbol,
    BigDecimal price,
    BigDecimal change,
    BigDecimal changePercent,
    Instant date
) {
}

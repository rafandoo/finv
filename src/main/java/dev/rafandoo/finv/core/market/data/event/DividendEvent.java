package dev.rafandoo.finv.core.market.data.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a dividend event for an instrument, which is a payment made by a company to its shareholders.
 *
 * @param date the date of the dividend payment.
 * @param amount the amount of the dividend payment per share.
 */
public record DividendEvent(
    Instant date,
    BigDecimal amount
) implements CorporateEvent {
}

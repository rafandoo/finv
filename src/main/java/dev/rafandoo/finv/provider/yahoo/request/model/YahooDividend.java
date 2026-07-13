package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.data.event.DividendEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a dividend event as provided by Yahoo Finance.
 *
 * @param date   The date of the dividend event.
 * @param amount The amount of the dividend.
 */
public record YahooDividend(
    Instant date,
    BigDecimal amount
) {

    /**
     * Converts this YahooDividend to a DividendEvent, which is a more general representation of a dividend event
     * used within the application.
     *
     * @return a DividendEvent representing the same dividend information as this YahooDividend.
     */
    public DividendEvent toDividendEvent() {
        return new DividendEvent(this.date, this.amount);
    }
}

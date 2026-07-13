package dev.rafandoo.finv.core.market.data.event;

import java.time.Instant;

/**
 * Represents a corporate event that affects the price of an instrument, such as dividends or stock splits.
 */
public sealed interface CorporateEvent permits DividendEvent, SplitEvent {

    /**
     * Gets the date of the corporate event.
     *
     * @return the date of the corporate event.
     */
    Instant date();
}

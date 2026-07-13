package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.data.event.SplitEvent;

import java.time.Instant;

/**
 * Represents a stock split event as provided by Yahoo Finance.
 *
 * @param date       The date of the split event.
 * @param splitRatio The ratio of the split (e.g., "2:1" for a 2-for-1 split).
 */
public record YahooSplit(
    Instant date,
    String splitRatio
) {

    /**
     * Converts this YahooSplit to a SplitEvent, which is a more general representation of a stock split event
     * used within the application.
     *
     * @return a SplitEvent representing the same split information as this YahooSplit.
     */
    public SplitEvent toSplitEvent() {
        return new SplitEvent(this.date, this.splitRatio);
    }
}

package dev.rafandoo.finv.core.market.data.event;

import java.time.Instant;

/**
 * Represents a stock split event for an instrument, which is a corporate action that increases the number of shares
 * outstanding while reducing the price per share.
 *
 * @param date the date of the stock split.
 * @param ratio the split ratio, which indicates how many new shares are issued for each existing share
 *              (e.g., "2:1" means each existing share is split into 2 new shares).
 */
public record SplitEvent(
    Instant date,
    String ratio
) implements CorporateEvent {
}

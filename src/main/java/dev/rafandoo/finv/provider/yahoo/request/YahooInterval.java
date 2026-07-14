package dev.rafandoo.finv.provider.yahoo.request;

import dev.rafandoo.finv.core.market.Interval;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;

/**
 * Maps the domain {@link Interval} to the wire-format token expected by the Yahoo Finance API.
 * Keeping the Yahoo-specific token in the provider layer avoids leaking wire formats into the
 * domain model.
 */
@UtilityClass
public final class YahooInterval {

    private static final Map<Interval, String> TOKENS = Map.ofEntries(
        Map.entry(Interval.DAILY, "1d"),
        Map.entry(Interval.WEEKLY, "5d"),
        Map.entry(Interval.MONTHLY, "1mo"),
        Map.entry(Interval.QUARTERLY, "3mo"),
        Map.entry(Interval.SEMI_ANNUAL, "6mo"),
        Map.entry(Interval.YEARLY, "1y"),
        Map.entry(Interval.BIENNIAL, "2y"),
        Map.entry(Interval.QUINQUENNIAL, "5y"),
        Map.entry(Interval.DECENNIAL, "10y"),
        Map.entry(Interval.YEAR_TO_DATE, "ytd"),
        Map.entry(Interval.MAXIMUM, "max")
    );

    /**
     * Returns the Yahoo wire-format token for the given domain interval.
     *
     * @param interval the domain interval (may be {@code null}, in which case the default is returned).
     * @return the corresponding Yahoo token, or {@code "1d"} when the interval is {@code null} or unmapped.
     */
    public static String token(Interval interval) {
        return Optional.ofNullable(interval)
            .map(TOKENS::get)
            .orElse("1d");
    }
}

package dev.rafandoo.finv.core.market;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a time range with a start and end instant, along with an interval for data aggregation.
 *
 * @param start    The starting instant of the time range (inclusive).
 * @param end      The ending instant of the time range (exclusive).
 * @param interval The interval for data aggregation (e.g., DAILY, HOURLY).
 */
public record TimeRange(
    Instant start,
    Instant end,
    Interval interval
) {

    public TimeRange {
        Objects.requireNonNull(start, "Start time cannot be null");
        Objects.requireNonNull(end, "End time cannot be null");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public static TimeRange of(Instant from, Instant to) {
        return new TimeRange(from, to, Interval.DAILY);
    }

    public static TimeRange of(Instant from, Instant to, Interval interval) {
        return new TimeRange(from, to, interval);
    }
}

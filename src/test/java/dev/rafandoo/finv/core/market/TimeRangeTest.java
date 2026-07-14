package dev.rafandoo.finv.core.market;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeRangeTest {

    private static final Instant START = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant END = Instant.parse("2023-01-31T23:59:59Z");

    @Test
    void shouldBuildWithInterval() {
        TimeRange range = new TimeRange(START, END, Interval.DAILY);
        assertThat(range.start()).isEqualTo(START);
        assertThat(range.end()).isEqualTo(END);
        assertThat(range.interval()).isEqualTo(Interval.DAILY);
    }

    @Test
    void shouldDefaultToDailyWhenOfCalled() {
        TimeRange range = TimeRange.of(START, END);
        assertThat(range.interval()).isEqualTo(Interval.DAILY);
        assertThat(range.start()).isEqualTo(START);
        assertThat(range.end()).isEqualTo(END);
    }

    @Test
    void shouldUseGivenIntervalWhenOfCalled() {
        TimeRange range = TimeRange.of(START, END, Interval.WEEKLY);
        assertThat(range.interval()).isEqualTo(Interval.WEEKLY);
    }

    @Test
    void shouldThrowWhenNullStart() {
        assertThatThrownBy(() -> new TimeRange(null, END, Interval.DAILY))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowWhenNullEnd() {
        assertThatThrownBy(() -> new TimeRange(START, null, Interval.DAILY))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowWhenStartAfterEnd() {
        assertThatThrownBy(() -> new TimeRange(END, START, Interval.DAILY))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start time must be before end time");
    }
}

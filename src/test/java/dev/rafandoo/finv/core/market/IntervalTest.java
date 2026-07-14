package dev.rafandoo.finv.core.market;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntervalTest {

    @Test
    void shouldExposeYahooIntervalValues() {
        assertThat(Interval.DAILY.getValue()).isEqualTo("1d");
        assertThat(Interval.WEEKLY.getValue()).isEqualTo("5d");
        assertThat(Interval.MONTHLY.getValue()).isEqualTo("1mo");
        assertThat(Interval.QUARTERLY.getValue()).isEqualTo("3mo");
        assertThat(Interval.YEARLY.getValue()).isEqualTo("1y");
        assertThat(Interval.YEAR_TO_DATE.getValue()).isEqualTo("ytd");
        assertThat(Interval.MAXIMUM.getValue()).isEqualTo("max");
    }

    @Test
    void shouldHaveExpectedNumberOfIntervals() {
        assertThat(Interval.values()).hasSize(11);
        assertThat(Interval.valueOf("DAILY")).isEqualTo(Interval.DAILY);
    }
}

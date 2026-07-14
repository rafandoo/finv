package dev.rafandoo.finv.provider.yahoo.request.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class YahooSplitTest {

    @Test
    void shouldExposeDateAndRatio() {
        YahooSplit split = new YahooSplit(Instant.parse("2021-01-01T00:00:00Z"), "4:1");
        assertThat(split.date()).isEqualTo(Instant.parse("2021-01-01T00:00:00Z"));
        assertThat(split.splitRatio()).isEqualTo("4:1");
    }
}

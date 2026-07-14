package dev.rafandoo.finv.core.market.data.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SplitEventTest {

    @Test
    void shouldExposeDateAndRatio() {
        SplitEvent split = new SplitEvent(Instant.parse("2021-08-31T00:00:00Z"), "4:1");
        assertThat(split.date()).isEqualTo(Instant.parse("2021-08-31T00:00:00Z"));
        assertThat(split.ratio()).isEqualTo("4:1");
    }
}

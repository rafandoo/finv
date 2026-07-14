package dev.rafandoo.finv.core.market.data.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DividendEventTest {

    @Test
    void shouldExposeDateAndAmount() {
        DividendEvent dividend = new DividendEvent(Instant.parse("2021-02-05T00:00:00Z"), new BigDecimal("0.205"));
        assertThat(dividend.date()).isEqualTo(Instant.parse("2021-02-05T00:00:00Z"));
        assertThat(dividend.amount()).isEqualTo(new BigDecimal("0.205"));
    }
}

package dev.rafandoo.finv.provider.yahoo.request.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteSummaryPriceTest {

    @Test
    void shouldExposeAllFields() {
        QuoteSummaryPrice price = new QuoteSummaryPrice(
            "AAPL",
            "Apple Inc.",
            "USD",
            "NASDAQ",
            new BigDecimal("150.25"),
            new BigDecimal("152.0"),
            new BigDecimal("149.0"),
            new BigDecimal("150.0"),
            new BigDecimal("148.0"),
            BigInteger.valueOf(123456),
            new BigDecimal("2.25"),
            new BigDecimal("1.5"),
            Instant.parse("2021-01-01T00:00:00Z")
        );

        assertThat(price.symbol()).isEqualTo("AAPL");
        assertThat(price.name()).isEqualTo("Apple Inc.");
        assertThat(price.currency()).isEqualTo("USD");
        assertThat(price.exchange()).isEqualTo("NASDAQ");
        assertThat(price.price()).isEqualTo(new BigDecimal("150.25"));
        assertThat(price.high()).isEqualTo(new BigDecimal("152.0"));
        assertThat(price.low()).isEqualTo(new BigDecimal("149.0"));
        assertThat(price.open()).isEqualTo(new BigDecimal("150.0"));
        assertThat(price.close()).isEqualTo(new BigDecimal("148.0"));
        assertThat(price.volume()).isEqualTo(BigInteger.valueOf(123456));
        assertThat(price.change()).isEqualTo(new BigDecimal("2.25"));
        assertThat(price.changePercent()).isEqualTo(new BigDecimal("1.5"));
        assertThat(price.marketTime()).isEqualTo(Instant.parse("2021-01-01T00:00:00Z"));
    }

    @Test
    void shouldConvertToQuoteAndCandle() {
        QuoteSummaryPrice price = new QuoteSummaryPrice(
            "AAPL", "Apple Inc.", "USD", "NASDAQ",
            new BigDecimal("150.25"), new BigDecimal("152.0"), new BigDecimal("149.0"),
            new BigDecimal("150.0"), new BigDecimal("148.0"), BigInteger.valueOf(123456),
            new BigDecimal("2.25"), new BigDecimal("1.5"), Instant.parse("2021-01-01T00:00:00Z")
        );
        assertThat(price.toQuote().symbol()).isEqualTo("AAPL");
        assertThat(price.toCandle().high()).isEqualTo(new BigDecimal("152.0"));
    }
}

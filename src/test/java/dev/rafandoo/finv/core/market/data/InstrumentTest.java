package dev.rafandoo.finv.core.market.data;

import dev.rafandoo.finv.core.market.asset.AssetType;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentTest {

    @Test
    void shouldExposeAllFields() {
        Instrument instrument = new Instrument(
            "AAPL", "Apple Inc.", "Apple Inc. Common Stock", AssetType.STOCK, "NASDAQ",
            Currency.getInstance("USD")
        );

        assertThat(instrument.symbol()).isEqualTo("AAPL");
        assertThat(instrument.name()).isEqualTo("Apple Inc.");
        assertThat(instrument.fullName()).isEqualTo("Apple Inc. Common Stock");
        assertThat(instrument.assetType()).isEqualTo(AssetType.STOCK);
        assertThat(instrument.exchange()).isEqualTo("NASDAQ");
        assertThat(instrument.currency()).isEqualTo(Currency.getInstance("USD"));
    }
}

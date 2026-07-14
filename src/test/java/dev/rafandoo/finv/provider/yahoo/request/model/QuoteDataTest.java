package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.data.Instrument;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteDataTest {

    @Test
    void shouldExposeAllFields() {
        QuoteData data = new QuoteData(
            "AAPL", "Apple Inc.", "Apple Inc.", AssetType.STOCK, "NASDAQ",
            Currency.getInstance("USD")
        );

        assertThat(data.symbol()).isEqualTo("AAPL");
        assertThat(data.shortName()).isEqualTo("Apple Inc.");
        assertThat(data.longName()).isEqualTo("Apple Inc.");
        assertThat(data.assetType()).isEqualTo(AssetType.STOCK);
        assertThat(data.exchangeName()).isEqualTo("NASDAQ");
        assertThat(data.currency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    void shouldConvertToInstrument() {
        QuoteData data = new QuoteData(
            "AAPL", "Apple Inc.", "Apple Inc.", AssetType.STOCK, "NASDAQ",
            Currency.getInstance("USD")
        );
        assertThat(data.toInstrument()).isInstanceOf(Instrument.class);
        assertThat(data.toInstrument().symbol()).isEqualTo("AAPL");
    }

    @Test
    void shouldBeValueBasedEquality() {
        QuoteData a = new QuoteData("AAPL", null, null, AssetType.STOCK, null, null);
        QuoteData b = new QuoteData("AAPL", null, null, AssetType.STOCK, null, null);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }
}

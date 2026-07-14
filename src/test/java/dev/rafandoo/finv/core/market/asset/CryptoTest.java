package dev.rafandoo.finv.core.market.asset;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CryptoTest {

    @Test
    void shouldBuildSymbolFromBaseAndQuote() {
        Crypto crypto = new Crypto("BTC", "USD", "BINANCE", Currency.getInstance("USD"));
        assertThat(crypto.base()).isEqualTo("BTC");
        assertThat(crypto.quote()).isEqualTo("USD");
        assertThat(crypto.exchange()).isEqualTo("BINANCE");
        assertThat(crypto.currency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(crypto.symbol()).isEqualTo("BTC/USD");
        assertThat(crypto.type()).isEqualTo(AssetType.CRYPTO);
    }

    @Test
    void shouldThrowWhenRequiredFieldsAreNull() {
        assertThatThrownBy(() -> new Crypto(null, "USD", "BINANCE", Currency.getInstance("USD")))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Crypto("BTC", null, "BINANCE", Currency.getInstance("USD")))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Crypto("BTC", "USD", null, Currency.getInstance("USD")))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void currencyIsOptional() {
        Crypto withCurrency = new Crypto("BTC", "USDT", "Binance", Currency.getInstance("BRL"));
        Crypto withoutCurrency = new Crypto("BTC", "USDT", "Binance", null);
        assertThat(withCurrency.currency()).isEqualTo(Currency.getInstance("BRL"));
        assertThat(withoutCurrency.currency()).isNull();
        // symbol() must not depend on currency
        assertThat(withoutCurrency.symbol()).isEqualTo("BTC/USDT");
    }
}

package dev.rafandoo.finv.core.market.asset;

import java.util.Currency;
import java.util.Objects;

/**
 * Represents a cryptocurrency asset traded on an exchange.
 *
 * @param base     the base currency symbol (e.g. "BTC").
 * @param quote    the quote currency of the trading pair (e.g. "USDT").
 * @param exchange the exchange where the asset is traded (e.g. "Binance").
 * @param currency the optional fiat currency used to quote the price (e.g. "USD");
 *                 null/blank means the provider default (USD) is used.
 */
public record Crypto(
    String base,
    String quote,
    String exchange,
    Currency currency
) implements Asset {

    public Crypto {
        Objects.requireNonNull(base);
        Objects.requireNonNull(quote);
        Objects.requireNonNull(exchange);
        // currency stays nullable / optional by design
    }

    @Override
    public String symbol() {
        return base + "/" + quote;
    }

    @Override
    public AssetType type() {
        return AssetType.CRYPTO;
    }
}

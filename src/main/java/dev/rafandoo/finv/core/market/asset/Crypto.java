package dev.rafandoo.finv.core.market.asset;

import java.util.Objects;

public record Crypto(
    String base,
    String quote,
    String exchange
) implements Asset {

    public Crypto {
        Objects.requireNonNull(base);
        Objects.requireNonNull(quote);
        Objects.requireNonNull(exchange);
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

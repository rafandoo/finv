package dev.rafandoo.finv.core.market.asset;

import java.util.Currency;
import java.util.Objects;

public record Stock(
    String symbol,
    String exchange,
    Currency currency
) implements Asset {

    public Stock {
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(exchange);
        Objects.requireNonNull(currency);
    }

    @Override
    public AssetType type() {
        return AssetType.STOCK;
    }

    public String qualifiedSymbol() {
        return exchange + ":" + symbol;
    }
}

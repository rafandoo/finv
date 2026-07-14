package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;

import java.util.Set;

/**
 * Static metadata for the CoinMarketCap keyless provider.
 */
public final class CoinMarketCapMetadata {

    private CoinMarketCapMetadata() {
    }

    public static final ProviderMetadata METADATA = new ProviderMetadata(
        "coinmarketcap",
        "CoinMarketCap",
        Set.of(AssetType.CRYPTO),
        Set.of(Feature.REALTIME)
    );
}

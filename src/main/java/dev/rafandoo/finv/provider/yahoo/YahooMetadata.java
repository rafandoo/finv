package dev.rafandoo.finv.provider.yahoo;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;

import java.util.Set;

/**
 * Metadata for the Yahoo Finance provider.
 */
public final class YahooMetadata {

    public static final ProviderMetadata METADATA = new ProviderMetadata(
        "yahoo",
        "Yahoo Finance",
        Set.of(AssetType.STOCK, AssetType.ETF, AssetType.FII, AssetType.INDEX),
        Set.of(Feature.HISTORY, Feature.REALTIME, Feature.DIVIDENDS, Feature.SPLITS, Feature.FUNDAMENTALS)
    );
}

package dev.rafandoo.finv.provider;

import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;

import java.util.ArrayList;
import java.util.List;

public class ProviderRegistry {

    private final List<MarketDataProvider> providers = new ArrayList<>();

    public MarketDataProvider resolve(Asset asset, Feature feature) {
        return providers.stream()
            .filter(p -> p.supports(asset.type()))
            .filter(p -> p.metadata().supportedFeatures().contains(feature))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No provider found for asset type " + asset.type() + " with feature " + feature));
    }
}

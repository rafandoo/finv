package dev.rafandoo.finv.provider;

import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;

import java.util.ArrayList;
import java.util.List;

public class ProviderRegistry {

    private final List<MarketDataProvider> providers = new ArrayList<>();

    /**
     * Registers a market data provider so it can be resolved for the asset types and features it supports.
     *
     * @param provider the provider to register.
     */
    public void register(MarketDataProvider provider) {
        providers.add(provider);
    }

    public MarketDataProvider resolve(Asset asset, Feature feature) {
        return providers.stream()
            .filter(p -> p.supports(asset.type()))
            .filter(p -> p.metadata().supportedFeatures().contains(feature))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No provider found for asset type " + asset.type() + " with feature " + feature));
    }
}

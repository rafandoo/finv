package dev.rafandoo.finv.core.market.provider;

import dev.rafandoo.finv.core.market.asset.AssetType;

import java.util.Set;

/**
 * Represents the metadata of a market data provider, including its supported asset types and features.
 *
 * @param id                  the unique identifier of the provider.
 * @param name                the human-readable name of the provider.
 * @param supportedAssetTypes the set of asset types that the provider supports.
 * @param supportedFeatures   the set of features that the provider supports.
 */
public record ProviderMetadata(
    String id,
    String name,
    Set<AssetType> supportedAssetTypes,
    Set<Feature> supportedFeatures
) {
}

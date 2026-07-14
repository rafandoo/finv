package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoinMarketCapMetadataTest {

    @Test
    void exposesCryptoAndRealtime() {
        ProviderMetadata metadata = CoinMarketCapMetadata.METADATA;
        assertThat(metadata.id()).isEqualTo("coinmarketcap");
        assertThat(metadata.supportedAssetTypes()).containsExactly(AssetType.CRYPTO);
        assertThat(metadata.supportedFeatures()).containsExactly(Feature.REALTIME);
    }
}

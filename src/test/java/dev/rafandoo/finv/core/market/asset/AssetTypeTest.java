package dev.rafandoo.finv.core.market.asset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssetTypeTest {

    @Test
    void shouldExposeAllAssetTypes() {
        assertThat(AssetType.values())
            .containsExactlyInAnyOrder(
                AssetType.STOCK,
                AssetType.CRYPTO,
                AssetType.ETF,
                AssetType.FII,
                AssetType.INDEX
            );
    }

    @Test
    void shouldHaveExpectedNameMapping() {
        assertThat(AssetType.STOCK.name()).isEqualTo("STOCK");
        assertThat(AssetType.valueOf("CRYPTO")).isEqualTo(AssetType.CRYPTO);
    }
}

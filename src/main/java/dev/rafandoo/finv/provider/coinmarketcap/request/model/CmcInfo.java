package dev.rafandoo.finv.provider.coinmarketcap.request.model;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.data.Instrument;

/**
 * Parsed metadata from CoinMarketCap /info for a single symbol.
 *
 * @param symbol      the slug symbol (e.g. "BTC").
 * @param name        the display name (e.g. "Bitcoin").
 * @param description the long description (may be null).
 * @param category    the CMC category (may be null).
 */
public record CmcInfo(
    String symbol,
    String name,
    String description,
    String category
) {

    /**
     * Maps this metadata to a domain {@link Instrument}.
     * Exchange and currency are left null here; the provider fills them in.
     *
     * @return the domain instrument.
     */
    public Instrument toInstrument() {
        return new Instrument(
            symbol,
            name != null ? name : symbol,
            description,
            AssetType.CRYPTO,
            null,
            null
        );
    }
}

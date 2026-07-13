package dev.rafandoo.finv.core.market.asset;

/*
 * An asset is a financial instrument that can be traded on the market.
 * It can be a stock, a cryptocurrency, or any other type of financial instrument.
 * The Asset interface is a sealed interface that can only be implemented by the Stock and Crypto classes.
 */
public sealed interface Asset permits Stock, Crypto {

    /**
     * Returns the symbol of the asset, which is a unique identifier for the asset.
     * For example, the symbol for Apple Inc. is AAPL, and the symbol for Bitcoin is BTC.
     *
     * @return the symbol of the asset.
     */
    String symbol();

    /**
     * Returns the type of the asset, which is an enum that indicates whether the asset is a
     * stock, a cryptocurrency, or any other type of financial instrument.
     *
     * @return the type of the asset.
     */
    AssetType type();

}

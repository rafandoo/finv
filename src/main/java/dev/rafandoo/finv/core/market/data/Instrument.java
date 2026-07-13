package dev.rafandoo.finv.core.market.data;

import dev.rafandoo.finv.core.market.asset.AssetType;

import java.util.Currency;

/**
 * Record representing a financial instrument, such as a stock, cryptocurrency, ETF, etc.
 *
 * @param symbol    The unique symbol identifying the instrument (e.g., "AAPL", "BTC-USD").
 * @param name      The short name of the instrument (e.g., "Apple Inc.", "Bitcoin").
 * @param fullName  The full descriptive name of the instrument (e.g., "Apple Inc. Common Stock", "Bitcoin USD Pair").
 * @param assetType The type of asset (e.g., STOCK, CRYPTO, ETF).
 * @param exchange  The exchange where the instrument is traded (e.g., "NASDAQ", "Binance").
 * @param currency  The currency in which the instrument is priced (e.g., BRL, EUR).
 */
public record Instrument(
    String symbol,
    String name,
    String fullName,
    AssetType assetType,
    String exchange,
    Currency currency
) {
}

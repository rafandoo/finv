package dev.rafandoo.finv.provider.yahoo.request.model;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.data.Instrument;

import java.util.Currency;

/**
 * Represents the quote data for a financial instrument.
 *
 * @param symbol       the ticker symbol of the instrument.
 * @param shortName    the short name of the instrument.
 * @param longName     the long name of the instrument.
 * @param assetType    the type of asset (e.g., STOCK, ETF).
 * @param exchangeName the name of the exchange where the instrument is traded.
 * @param currency     the currency in which the instrument is priced.
 */
public record QuoteData(
    String symbol,
    String shortName,
    String longName,
    AssetType assetType,
    String exchangeName,
    Currency currency
) {

    /**
     * Converts this QuoteData to an Instrument object.
     *
     * @return an {@link Instrument} object containing the data from this QuoteData.
     */
    public Instrument toInstrument() {
        return new Instrument(
            symbol,
            shortName,
            longName,
            assetType,
            exchangeName,
            currency
        );
    }
}

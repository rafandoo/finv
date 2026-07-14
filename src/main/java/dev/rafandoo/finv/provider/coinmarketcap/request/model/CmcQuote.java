package dev.rafandoo.finv.provider.coinmarketcap.request.model;

import dev.rafandoo.finv.core.market.data.Quote;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;

/**
 * Parsed real-time quote from CoinMarketCap /quotes/latest for a single symbol+convert pair.
 *
 * @param symbol        the asset symbol requested (e.g. "BTC").
 * @param price         the latest price in the convert currency.
 * @param volume24h     the 24h volume in the convert currency (may be null).
 * @param changePercent the 24h percentage change (may be null).
 * @param lastUpdated   the quote timestamp (may be null).
 * @param convert       the fiat currency used (e.g. "USD").
 */
public record CmcQuote(
    String symbol,
    BigDecimal price,
    BigDecimal volume24h,
    BigDecimal changePercent,
    Instant lastUpdated,
    String convert
) {

    /**
     * Maps this quote to a domain {@link Quote}, deriving the absolute change from the 24h percent.
     *
     * @param symbol the domain symbol (e.g. "BTC/USDT").
     * @return the domain quote.
     */
    public Quote toQuote(String symbol) {
        BigDecimal change = null;
        if (price != null && changePercent != null) {
            BigDecimal divisor = BigDecimal.ONE.add(
                changePercent.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64));
            BigDecimal base = price.divide(divisor, MathContext.DECIMAL64);
            change = price.subtract(base);
        }
        return new Quote(symbol, price, change, changePercent, lastUpdated);
    }
}

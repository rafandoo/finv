package dev.rafandoo.finv.provider.coinmarketcap.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import dev.rafandoo.finv.provider.coinmarketcap.request.parse.QuoteParser;

import static dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost.PUBLIC;

/**
 * Request for {@code GET /quotes/latest?symbol=<symbol>&convert=<currency>}.
 */
public final class LatestQuoteRequest implements CoinMarketCapRequest<CmcQuote> {

    private final String symbol;
    private final String currency;

    public LatestQuoteRequest(String symbol, String currency) {
        this.symbol = symbol;
        this.currency = currency;
    }

    @Override
    public UrlBuilder url() {
        return UrlBuilder.builder()
            .https()
            .host(PUBLIC.host())
            .path("public-api", "quotes", "latest")
            .addParameter("symbol", symbol)
            .addParameter("convert", currency);
    }

    @Override
    public ResponseParser<CmcQuote> parser() {
        return new QuoteParser(symbol, currency);
    }
}

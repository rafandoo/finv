package dev.rafandoo.finv.provider.coinmarketcap.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.parse.InfoParser;

import static dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost.PUBLIC;

/**
 * Request for {@code GET /info?symbol=<symbol>}.
 */
public final class InfoRequest implements CoinMarketCapRequest<CmcInfo> {

    private final String symbol;

    public InfoRequest(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public UrlBuilder url() {
        return UrlBuilder.builder()
            .https()
            .host(PUBLIC.host())
            .path("public-api", "info")
            .addParameter("symbol", symbol);
    }

    @Override
    public ResponseParser<CmcInfo> parser() {
        return new InfoParser(symbol);
    }
}

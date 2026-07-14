package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.YahooRequestBuilder;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooApiVersion;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooService;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteData;
import dev.rafandoo.finv.provider.yahoo.request.parse.QuoteParser;

/**
 * A request to retrieve basic quote information for a given symbol from the Yahoo Finance API.
 */
public final class QuoteRequest implements YahooRequest<QuoteData> {

    private final String symbol;

    public QuoteRequest(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public UrlBuilder url() {
        return YahooRequestBuilder.builder()
            .host(YahooHost.QUERY2)
            .version(YahooApiVersion.V7)
            .service(YahooService.QUOTE)
            .symbolAsParam(this.symbol)
            .param("formatted", "false")
            .create();
    }

    @Override
    public ResponseParser<QuoteData> parser() {
        return new QuoteParser();
    }
}

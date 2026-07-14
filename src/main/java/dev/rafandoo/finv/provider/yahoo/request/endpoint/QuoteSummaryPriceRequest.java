package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.YahooRequestBuilder;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteSummaryPrice;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooApiVersion;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooService;
import dev.rafandoo.finv.provider.yahoo.request.parse.QuoteSummaryPriceParser;

/**
 * A request to retrieve detailed price information for a given symbol from the Yahoo Finance API.
 */
public final class QuoteSummaryPriceRequest implements YahooRequest<QuoteSummaryPrice> {

    private final String symbol;

    public QuoteSummaryPriceRequest(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public UrlBuilder url() {
        return YahooRequestBuilder.builder()
            .host(YahooHost.QUERY2)
            .version(YahooApiVersion.V10)
            .service(YahooService.QUOTE_SUMMARY)
            .symbol(this.symbol)
            .param("modules", "price")
            .param("formatted", "false")
            .create();
    }

    @Override
    public ResponseParser<QuoteSummaryPrice> parser() {
        return new QuoteSummaryPriceParser();
    }
}

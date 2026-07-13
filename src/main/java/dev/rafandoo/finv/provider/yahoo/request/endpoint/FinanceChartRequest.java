package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.YahooRequestBuilder;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooApiVersion;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooEvent;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooService;
import dev.rafandoo.finv.provider.yahoo.request.model.ChartAggregate;
import dev.rafandoo.finv.provider.yahoo.request.parse.FinanceChartParser;

/**
 * A request to retrieve historical chart data for a given symbol and time range from the Yahoo Finance API.
 */
public final class FinanceChartRequest implements YahooRequest<ChartAggregate> {

    private final String symbol;
    private final TimeRange timeRange;
    private final YahooEvent event;

    public FinanceChartRequest(String symbol, TimeRange timeRange) {
        this.symbol = symbol;
        this.timeRange = timeRange;
        this.event = YahooEvent.HISTORY;
    }

    public FinanceChartRequest(String symbol, TimeRange timeRange, YahooEvent event) {
        this.symbol = symbol;
        this.timeRange = timeRange;
        this.event = event;
    }

    @Override
    public UrlBuilder url() {
        return YahooRequestBuilder.builder()
            .host(YahooHost.QUERY2)
            .version(YahooApiVersion.V8)
            .service(YahooService.CHART)
            .symbol(this.symbol)
            .param("formatted", "false")
            .param("interval", this.timeRange.interval() == null ? "1d" : this.timeRange.interval().getValue())
            .param("period1", this.timeRange.start().getEpochSecond())
            .param("period2", this.timeRange.end().getEpochSecond())
            .param("events", this.event.equals(YahooEvent.HISTORY) ? "" : this.event.value())
            .create();
    }

    @Override
    public ResponseParser<ChartAggregate> parser() {
        return new FinanceChartParser(this.event);
    }
}

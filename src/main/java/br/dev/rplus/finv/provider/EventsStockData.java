package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.utils.DateUtils;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.enums.Event;
import br.dev.rplus.finv.enums.Frequency;
import br.dev.rplus.finv.enums.RequestParams;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches historical stock data for specific events (such as dividends, splits, or historical quotes)
 * from an external API and populates the associated {@link Stock} object.
 */
public class EventsStockData extends AbstractStockDataProvider {

    private final String startDate;
    private final String endDate;
    private final Frequency frequency;
    private final List<Event> events;

    /**
     * Constructs an {@code EventsStockData} object for fetching stock event data.
     *
     * @param stock     the {@link Stock} object to be populated with the fetched data.
     * @param startDate the start date of the data range (format: "yyyy-MM-dd").
     * @param endDate   the end date of the data range (format: "yyyy-MM-dd").
     * @param frequency the {@link Frequency} of the data (e.g., daily, weekly).
     * @param events    a list of {@link Event} types to fetch (e.g., dividends, splits).
     */
    public EventsStockData(Stock stock, String startDate, String endDate, Frequency frequency, List<Event> events) {
        super(stock);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.events = events;
    }

    /**
     * Builds a string representing the requested events for the API query.
     * Multiple events are concatenated with a pipe ('|') separator.
     *
     * @return a string representing the requested events (e.g., "div|split|history").
     */
    private String getEvents() {
        StringBuilder sb = new StringBuilder();
        if (this.events != null) {
            for (var event : this.events) {
                sb.append(event.getName());
                if (this.events.indexOf(event) < this.events.size() - 1) {
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected Map<String, String> getRequestParameters() {
        Map<String, String> params = new LinkedHashMap<>();
        DateUtils du = new DateUtils();
        try {
            params.put("period1", DateUtils.toTimestamp(du.parse(this.startDate)));
            params.put("period2", DateUtils.toTimestamp(du.parse(this.endDate)));
            params.put("interval", this.frequency.getName());
            params.put("events", this.getEvents());
            params.put("includeAdjustedClose", "true");
        } catch (Exception e) {
            logger.warn("Error building the request parameters.", e);
        }
        return params;
    }

    @Override
    protected String getApiUrl() {
        return prepareUrl(RequestParams.STOCKS_QUERY_URL_V8.get().asString());
    }

    @Override
    protected void parseApiResponse(String response) {
        if (this.events != null) {
            this.events.forEach(event -> {
                switch (event) {
                    case DIVIDENDS -> getStock().setDividendHistory(StockDividendData.getInstance().parse(response));
                    case SPLIT -> getStock().setSplitHistory(StockSplitData.getInstance().parse(response));
                    case HISTORY -> getStock().setQuoteHistory(HistoricalStockData.getInstance().parse(response));
                }
            });
        }
    }
}

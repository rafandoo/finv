package br.dev.rplus.finv.provider;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.config.Paramters;
import br.dev.rplus.finv.data.Frequency;
import br.dev.rplus.finv.data.StockHistoricalQuote;
import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.cup.others.DateFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;

public class HistoricalStockData extends AbstractStockDataProvider {

    private final String startDate;
    private final String endDate;
    private final Frequency frequency;

    public HistoricalStockData(Stock stock, String startDate, String endDate, Frequency frequency) {
        super(stock);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
    }

    /**
     * The function returns a map of request parameters for a Java program, including start and end
     * dates, frequency, and a flag for including adjusted close prices.
     * 
     * @return The method is returning a Map object that contains the request parameters.
     */
    @Override
    protected Map<String, String> getRequestParameters() {
        Map<String, String> params = new LinkedHashMap<>();
        try {
            params.put("period1", DateFormatter.timestampFormat(DateFormatter.parse(startDate)));
            params.put("period2", DateFormatter.timestampFormat(DateFormatter.parse(endDate)));
            params.put("interval", frequency.getName());
            params.put("includeAdjustedClose", "true");
        } catch (Exception e) {
            LoggerCup.warn("Error building the request parameters. ", e);
            return Collections.emptyMap();
        }
        return params;
    }

    /**
     * The function returns the API URL for querying stock information, with the stock ticker encoded
     * in the URL.
     * 
     * @return The method is returning a string value.
     */
    @Override
    protected String getApiUrl() {
        try {
            return Paramters.STOCKS_QUERY_URL_V8 + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (Exception e) {
            LoggerCup.warn("Error building the API URL.", e);
            return null;
        }
    }

    /**
     * The function parses a JSON API response and extracts historical stock quotes, storing them in a
     * list.
     * 
     * @param response The response parameter is a string that contains the API response in JSON
     * format.
     */
    @Override
    protected void parseApiResponse(String response) {
        List<StockHistoricalQuote> historicalQuotes = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject chartObject = jsonResponse.getJSONObject("chart");
            JSONObject resultObject = chartObject.getJSONArray("result").getJSONObject(0);
            JSONObject indicatorsObject = resultObject.getJSONObject("indicators");
            JSONObject quoteObject = indicatorsObject.getJSONArray("quote").getJSONObject(0);

            JSONArray timestampArray = resultObject.getJSONArray("timestamp");

            JSONArray openArray = quoteObject.getJSONArray("open");
            JSONArray closeArray = quoteObject.getJSONArray("close");
            JSONArray highArray = quoteObject.getJSONArray("high");
            JSONArray lowArray = quoteObject.getJSONArray("low");
            JSONArray volumeArray = quoteObject.getJSONArray("volume");

            for (int i = 0; i < timestampArray.length(); i++) {
                StockHistoricalQuote historicalQuote = new StockHistoricalQuote();
                historicalQuote.setDate(new Date(timestampArray.getLong(i) * 1000));
                historicalQuote.setOpen(openArray.getDouble(i));
                historicalQuote.setClose(closeArray.getDouble(i));
                historicalQuote.setHigh(highArray.getDouble(i));
                historicalQuote.setLow(lowArray.getDouble(i));
                historicalQuote.setVolume(volumeArray.getLong(i));
                historicalQuotes.add(historicalQuote);
            }
            LoggerCup.info("Historical stock quotes fetched successfully for %s", stock.getTicker());
            stock.setQuoteHistory(historicalQuotes);
        } catch (Exception e) {
            LoggerCup.warn("Error parsing the API response.", e);
        }
    }
}

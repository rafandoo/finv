package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.config.Paramters;
import br.dev.rplus.finv.data.Event;
import br.dev.rplus.finv.data.Frequency;
import br.dev.rplus.finv.data.StockSplit;
import br.dev.rplus.cup.others.DateFormatter;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;

public class StockSplitData extends AbstractStockDataProvider {
    private final String startDate;
    private final String endDate;
    private final Frequency frequency;

    public StockSplitData(Stock stock, String startDate, String endDate, Frequency frequency) {
        super(stock);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
    }

    /**
     * The function returns a map of request parameters for a Java program, including start and end
     * dates, frequency, event type, and a flag for including adjusted close prices.
     * 
     * @return The method is returning a Map object with String keys and String values.
     */
    @Override
    protected Map<String, String> getRequestParameters() {
        Map<String, String> params = new LinkedHashMap<>();
        try {
            params.put("period1", DateFormatter.timestampFormat(DateFormatter.parse(startDate)));
            params.put("period2", DateFormatter.timestampFormat(DateFormatter.parse(endDate)));
            params.put("interval", frequency.getName());
            params.put("events", Event.SPLIT.getName());
            params.put("includeAdjustedClose", "true");
        } catch (Exception e) {
            LoggerCup.warn("Error building the request parameters.", e);
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
     * The function parses an API response to extract stock split information and sets it in a Stock
     * object.
     * 
     * @param response The response parameter is a string that represents the API response received
     * from a server.
     */
    @Override
    protected void parseApiResponse(String response) {
        List<StockSplit> splits = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject chartObject = jsonResponse.getJSONObject("chart");
            JSONObject resultObject = chartObject.getJSONArray("result").getJSONObject(0);
            JSONObject indicatorsObject = resultObject.getJSONObject("events");

            if (indicatorsObject.has("splits")) {
                JSONObject dividendsObject = indicatorsObject.getJSONObject("splits");

                for (String timestamp : dividendsObject.keySet()) {
                    JSONObject dividendInfo = dividendsObject.getJSONObject(timestamp);

                    String splitRatio = dividendInfo.getString("splitRatio");
                    Date date = new Date(Long.parseLong(timestamp) * 1000);

                    StockSplit split = new StockSplit(splitRatio, date);
                    splits.add(split);
                }
            }
            LoggerCup.info("Parsed stock quote for %s.", stock.getTicker());
            stock.setSplitHistory(splits);
        } catch (Exception e) {
            LoggerCup.warn("Error parsing the API response.", e);
        }
    }
}

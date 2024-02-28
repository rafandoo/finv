package finv.provider;

import finv.Finv;
import finv.Stock;
import finv.data.Event;
import finv.data.Frequency;
import finv.data.StockSplit;
import finv.util.DateFormatter;
import finv.util.LogConfig;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

public class StockSplitData extends AbstractStockDataProvider {

    private static final Logger logger = LogConfig.getLogger();
    private final String startDate;
    private final String endDate;
    private final Frequency frequency;

    public StockSplitData(Stock stock, String startDate, String endDate, Frequency frequency) {
        super(stock);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        LogConfig.configure();
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
            logger.warning("Error building the request parameters: " + e.getMessage());
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
            return Finv.STOCKS_QUERY_URL_V8 + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (Exception e) {
            logger.warning("Error building the API URL: " + e.getMessage());
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
            logger.info("Stock split data fetched successfully for " + stock.getTicker());
            stock.setSplitHistory(splits);
        } catch (Exception e) {
            logger.warning("Error parsing the API response: " + e.getMessage());
        }
    }
}

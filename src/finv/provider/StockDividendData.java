package finv.provider;

import finv.Finv;
import finv.Stock;
import finv.data.Event;
import finv.data.Frequency;
import finv.data.StockDividend;
import finv.util.DateFormatter;
import finv.util.LogConfig;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

public class StockDividendData extends AbstractStockDataProvider {

    private static final Logger logger = LogConfig.getLogger();
    private final String startDate;
    private final String endDate;
    private final Frequency frequency;

    public StockDividendData(Stock stock, String startDate, String endDate, Frequency frequency) {
        super(stock);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        LogConfig.configure();
    }


    /**
     * The function returns a map of request parameters for a Java program, including start and end dates,
     * frequency, events, and a flag for including adjusted close values.
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
            params.put("events", Event.DIVIDENDS.getName());
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
     * The function parses a JSON API response to extract dividend information and sets it in a Stock
     * object.
     * 
     * @param response The response parameter is a string that contains the API response in JSON
     * format.
     */
    @Override
    protected void parseApiResponse(String response) {
        List<StockDividend> dividends = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject chartObject = jsonResponse.getJSONObject("chart");
            JSONObject resultObject = chartObject.getJSONArray("result").getJSONObject(0);
            JSONObject indicatorsObject = resultObject.getJSONObject("events");

            if (indicatorsObject.has("dividends")) {
                JSONObject dividendsObject = indicatorsObject.getJSONObject("dividends");

                for (String timestamp : dividendsObject.keySet()) {
                    JSONObject dividendInfo = dividendsObject.getJSONObject(timestamp);

                    double amount = dividendInfo.getDouble("amount");
                    Date date = new Date(Long.parseLong(timestamp) * 1000); // Convert timestamp to milliseconds

                    StockDividend dividend = new StockDividend(amount, date);
                    dividends.add(dividend);
                }
            }
            logger.info("Dividend data fetched successfully for " + stock.getTicker());
            stock.setDividendHistory(dividends);
        } catch (Exception e) {
            logger.warning("Error parsing the API response: " + e.getMessage());
        }
    }
}

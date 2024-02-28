package finv.provider;

import finv.Finv;
import finv.Stock;
import finv.util.CrumbYahoo;
import finv.util.LogConfig;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class StockData extends AbstractStockDataProvider {

    private static final Logger logger = LogConfig.getLogger();

    public StockData(Stock stock) {
        super(stock);
        LogConfig.configure();
    }


    /**
     * The function returns the API URL for a given stock ticker, encoded in UTF-8.
     * 
     * @return The method is returning a string value.
     */
    @Override
    protected String getApiUrl() {
        try {
            return Finv.STOCKS_QUERY_URL_V7 + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warning("Error building the API URL: " + e.getMessage());
            return null;
        }
    }

    /**
     * The function returns an empty map of request parameters.
     * 
     * @return An empty map of type `Map String, String` is being returned.
     */
    @Override
    protected Map<String, String> getRequestParameters() {
       Map<String, String> params = new LinkedHashMap<>();
       try {
           params.put("crumb", CrumbYahoo.getCrumb());
       } catch (Exception e) {
           logger.warning("Error building the request parameters: " + e.getMessage());
           return Collections.emptyMap();
       }
       return params;
    }

    /**
     * The function parses a JSON API response to extract specific data and sets the values in a Stock
     * object.
     * 
     * @param response The response parameter is a string that contains the API response in JSON format.
     */
    @Override
    protected void parseApiResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject optionChainObject = jsonResponse.getJSONObject("optionChain");
            JSONObject resultObject = optionChainObject.getJSONArray("result").getJSONObject(0);
            JSONObject quoteObject = resultObject.getJSONObject("quote");

            stock.setCurrency(quoteObject.getString("currency"));
            stock.setName(quoteObject.getString("longName"));
            stock.setStockExchange(quoteObject.getString("fullExchangeName"));
            stock.setQuoteType(quoteObject.getString("quoteType"));
            logger.info("Stock data fetched successfully for " + stock.getTicker());
        } catch (Exception e) {
            logger.warning("Error parsing the API response: " + e.getMessage());
            logger.warning("May be the stock ticker is invalid: " + stock.getTicker() + ", please check the stock ticker and try again.");
        }
    }
}

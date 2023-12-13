package finv.provider;

import finv.Finv;
import finv.Stock;
import finv.util.LogConfig;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
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
            return Finv.STOCKS_BASE_URL + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.severe("Error building the API URL: " + e.getMessage());
            return null;
        }
    }

    /**
     * The function returns an empty map of request parameters.
     * 
     * @return An empty map of type `Map<String, String>` is being returned.
     */
    @Override
    protected Map<String, String> getRequestParameters() {
        return Collections.emptyMap();
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

        } catch (Exception e) {
            logger.severe("Error parsing the API response: " + e.getMessage());
        }
    }
}

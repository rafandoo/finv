package finv.provider;

import finv.Finv;
import finv.Stock;
import finv.data.StockQuote;
import finv.util.LogConfig;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public class StockQuoteData extends AbstractStockDataProvider {

    private static final Logger logger = LogConfig.getLogger();

    public StockQuoteData(Stock stock) {
        super(stock);
        LogConfig.configure();
    }

    /**
     * The function returns the API URL for a given stock ticker, encoding it using UTF-8.
     * 
     * @return The method is returning a string value.
     */
    @Override
    protected String getApiUrl() {
        try {
            return Finv.STOCKS_BASE_URL + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (Exception e) {
            logger.severe("Error building the API URL: " + e.getMessage());
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
        return Collections.emptyMap();
    }

    /**
     * The function parses a JSON response from an API and extracts relevant data to populate a
     * StockQuote object.
     * 
     * @param response The response parameter is a string that contains the API response in JSON
     * format.
     */
    @Override
    protected void parseApiResponse(String response) {
        StockQuote stockQuote = new StockQuote();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject optionChainObject = jsonResponse.getJSONObject("optionChain");
            JSONObject resultObject = optionChainObject.getJSONArray("result").getJSONObject(0);
            JSONObject quoteObject = resultObject.getJSONObject("quote");

            stockQuote.setPrice(quoteObject.getDouble("regularMarketPrice"));
            stockQuote.setChange(quoteObject.getDouble("regularMarketChange"));
            stockQuote.setOpen(quoteObject.getDouble("regularMarketOpen"));
            stockQuote.setPreviousClose(quoteObject.getDouble("regularMarketPreviousClose"));
            stockQuote.setVolume(quoteObject.getLong("regularMarketVolume"));
            stockQuote.setBid(quoteObject.getDouble("bid"));
            stockQuote.setAsk(quoteObject.getDouble("ask"));

            stock.setQuote(stockQuote);

        } catch (Exception e) {
            logger.severe("Error parsing the API response: " + e.getMessage());
        }
    }
}

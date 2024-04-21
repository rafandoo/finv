package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.config.Paramters;
import br.dev.rplus.finv.util.CrumbYahoo;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StockData extends AbstractStockDataProvider {

    public StockData(Stock stock) {
        super(stock);
    }


    /**
     * The function returns the API URL for a given stock ticker, encoded in UTF-8.
     * 
     * @return The method is returning a string value.
     */
    @Override
    protected String getApiUrl() {
        try {
            return Paramters.STOCKS_QUERY_URL_V7 + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LoggerCup.warn("Error building the API URL.", e);
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
           LoggerCup.warn("Error building the request parameters.", e);
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
            LoggerCup.info("Stock data fetched successfully for %s", stock.getTicker());
        } catch (Exception e) {
            LoggerCup.warn("Error parsing the API response.", e);
            LoggerCup.warn("May be the stock ticker is invalid: %s, please check the stock ticker and try again.", stock.getTicker());
        }
    }
}

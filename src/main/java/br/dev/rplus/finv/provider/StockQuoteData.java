package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.config.Paramters;
import br.dev.rplus.finv.data.StockQuote;
import br.dev.rplus.finv.util.CrumbYahoo;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StockQuoteData extends AbstractStockDataProvider {

    public StockQuoteData(Stock stock) {
        super(stock);
    }

    /**
     * The function returns the API URL for a given stock ticker, encoding it using UTF-8.
     * 
     * @return The method is returning a string value.
     */
    @Override
    protected String getApiUrl() {
        try {
            return Paramters.STOCKS_QUERY_URL_V7 + URLEncoder.encode(stock.getTicker(), "UTF-8");
        } catch (Exception e) {
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

            LoggerCup.info("Parsed stock quote for %s.", stock.getTicker());
            stock.setQuote(stockQuote);
        } catch (Exception e) {
            LoggerCup.warn("Error parsing the API response.", e);
        }
    }
}

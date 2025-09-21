package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.utils.DateUtils;
import br.dev.rplus.cup.utils.Parser;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockQuote;
import br.dev.rplus.finv.enums.RequestParams;
import br.dev.rplus.finv.provider.yahoo.CrumbYahoo;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class responsible for fetching and parsing detailed stock information from the API.
 * <p>
 * This class retrieves essential information about a stock, including its name, currency, market details, and current quote.
 * It extends the {@link AbstractStockDataProvider} and overrides methods to define request parameters, construct the API URL,
 * and parse the API response into a {@link Stock} object with an associated {@link StockQuote}.
 */
public class StockData extends AbstractStockDataProvider {

    /**
     * Constructs a new {@code StockData} instance for the specified {@link Stock}.
     *
     * @param stock The stock for which data will be fetched.
     */
    public StockData(Stock stock) {
        super(stock);
    }

    @Override
    protected String getApiUrl() {
        return prepareUrl(RequestParams.STOCKS_QUERY_URL_V7.get().asString());
    }

    @Override
    protected Map<String, String> getRequestParameters() {
        Map<String, String> params = new LinkedHashMap<>();
        try {
            params.put("crumb", CrumbYahoo.getCrumb());
        } catch (Exception e) {
            logger.warn("Error building the request parameters.", e);
        }
        return params;
    }

    @Override
    protected void parseApiResponse(String response) {
        StockQuote stockQuote;
        JSONObject oResponse = null;

        try {
            oResponse = new JSONObject(response)
                .getJSONObject("optionChain")
                .getJSONArray("result")
                .getJSONObject(0)
                .getJSONObject("quote");

            getStock().setName(oResponse.optString("longName"));
            getStock().setCurrency(oResponse.optString("currency"));
            getStock().setMarket(oResponse.optString("fullExchangeName"));
            getStock().setMarketState(oResponse.optString("marketState"));
            getStock().setQuoteType(oResponse.optString("quoteType"));
            getStock().setRegion(oResponse.optString("region"));
            getStock().setExchangeTimezone(oResponse.optString("exchangeTimezoneName"));
            getStock().setQuoteSource(oResponse.optString("quoteSourceName"));

            stockQuote = new StockQuote(
                DateUtils.fromTimestamp(String.valueOf(oResponse.get("regularMarketTime"))),
                Parser.toDouble(oResponse.get("regularMarketPrice")),
                Parser.toDouble(oResponse.get("regularMarketChange")),
                Parser.toDouble(oResponse.get("regularMarketOpen")),
                Parser.toDouble(oResponse.get("regularMarketPreviousClose")),
                Parser.toDouble(oResponse.get("regularMarketDayLow")),
                Parser.toDouble(oResponse.get("regularMarketDayHigh")),
                Parser.toLong(oResponse.get("regularMarketVolume")),
                Parser.toDouble(oResponse.get("bid")),
                Parser.toDouble(oResponse.get("ask"))
            );
            getStock().setQuote(stockQuote);

            logger.info("Stock data fetched successfully for %s", getStock().getTicker());
        } catch (Exception e) {
            logger.warn("Error parsing the API response.", e);
            logger.warn("The stock ticker might be invalid: %s. Please check the ticker and try again.", getStock().getTicker());
        } finally {
            if (oResponse != null) oResponse.clear();
        }
    }
}

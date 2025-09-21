package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.cup.utils.Parser;
import br.dev.rplus.finv.data.StockHistoricalQuote;
import br.dev.rplus.cup.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Singleton class that fetches and parses historical stock data from an API response.
 * <p>
 * Implements the {@link EventParse} interface to convert the API response into a list of {@link StockHistoricalQuote} objects.
 * This class extracts information such as open, close, high, low, volume, and adjusted close prices for a stock within a given time range.
 */
public class HistoricalStockData implements EventParse<StockHistoricalQuote> {

    private static HistoricalStockData instance;
    private final Logger logger = Logger.getInstance();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private HistoricalStockData() {}

    /**
     * Returns the singleton instance of {@code HistoricalStockData}.
     *
     * @return the singleton instance of {@code HistoricalStockData}.
     */
    public static HistoricalStockData getInstance() {
        if (instance == null) {
            instance = new HistoricalStockData();
        }
        return instance;
    }

    @Override
    public List<StockHistoricalQuote> parse(String response) {
        List<StockHistoricalQuote> historicalQuotes = new ArrayList<>();
        JSONObject oResponse = null;

        try {
            oResponse = new JSONObject(response)
                .getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0);

            JSONArray timestamps = oResponse.getJSONArray("timestamp");

            JSONObject oQuote = oResponse.getJSONObject("indicators")
                .getJSONArray("quote")
                .getJSONObject(0);

            JSONArray openArray = oQuote.getJSONArray("open");
            JSONArray closeArray = oQuote.getJSONArray("close");
            JSONArray highArray = oQuote.getJSONArray("high");
            JSONArray lowArray = oQuote.getJSONArray("low");
            JSONArray volumeArray = oQuote.getJSONArray("volume");

            JSONArray adjCloseArray = oResponse.getJSONObject("indicators")
                .getJSONArray("adjclose")
                .getJSONObject(0)
                .getJSONArray("adjclose");

            for (int i = 0; i < timestamps.length(); i++) {
                StockHistoricalQuote historicalQuote = new StockHistoricalQuote(
                    DateUtils.fromTimestamp(String.valueOf(timestamps.get(i))),
                    Parser.toDouble(openArray.get(i)),
                    Parser.toDouble(closeArray.get(i)),
                    Parser.toDouble(adjCloseArray.get(i)),
                    Parser.toDouble(lowArray.get(i)),
                    Parser.toDouble(highArray.get(i)),
                    Parser.toLong(volumeArray.get(i))
                );
                historicalQuotes.add(historicalQuote);
            }
            this.logger.info("Historical stock quotes fetched successfully.");
        } catch (Exception e) {
            this.logger.warn("Error parsing the API response.", e);
        } finally {
            if (oResponse != null) oResponse.clear();
        }
        return historicalQuotes;
    }
}

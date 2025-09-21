package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.cup.utils.DateUtils;
import br.dev.rplus.cup.utils.Parser;
import br.dev.rplus.finv.data.StockDividend;
import org.json.JSONObject;

import java.util.*;

/**
 * Singleton class responsible for fetching and parsing stock dividend data from the API.
 * <p>
 * This class implements the {@link EventParse} interface to transform the API response into a list of {@link StockDividend} objects.
 * It extracts dividend-related information, such as the amount and date, from the response JSON.
 */
public class StockDividendData implements EventParse<StockDividend> {

    private static StockDividendData instance;
    private final Logger logger = Logger.getInstance();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private StockDividendData() {}

    /**
     * Returns the singleton instance of {@code StockDividendData}.
     *
     * @return the singleton instance of {@code StockDividendData}.
     */
    public static StockDividendData getInstance() {
        if (instance == null) {
            instance = new StockDividendData();
        }
        return instance;
    }

    @Override
    public List<StockDividend> parse(String response) {
        List<StockDividend> dividends = new ArrayList<>();
        JSONObject oResponse = null;

        try {
            oResponse = new JSONObject(response)
                .getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0)
                .getJSONObject("events");

            if (oResponse.has("dividends")) {
                JSONObject oDividends = oResponse.getJSONObject("dividends");

                for (String timestamp : oDividends.keySet()) {
                    JSONObject dividendInfo = oDividends.getJSONObject(timestamp);

                    double amount = Parser.toDouble(dividendInfo.get("amount"));
                    Date date = DateUtils.fromTimestamp(timestamp);

                    dividends.add(new StockDividend(amount, date));
                }
            }
            this.logger.info("Dividend data fetched successfully.");
        } catch (Exception e) {
            this.logger.warn("Error parsing the API response.", e);
        } finally {
            if (oResponse != null) oResponse.clear();
        }
        return dividends;
    }
}

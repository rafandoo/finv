package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.finv.data.StockSplit;
import br.dev.rplus.cup.utils.DateUtils;
import org.json.JSONObject;

import java.util.*;

/**
 * Singleton class responsible for fetching and parsing stock split data from the API.
 * <p>
 * This class implements the {@link EventParse} interface to transform the API response into a list of {@link StockSplit} objects.
 * It extracts stock split events, including the split ratio and the corresponding date, from the response JSON.
 */
public class StockSplitData implements EventParse<StockSplit> {

    private static StockSplitData instance;
    private final Logger logger = Logger.getInstance();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private StockSplitData() {}

    /**
     * Returns the singleton instance of {@code StockSplitData}.
     *
     * @return the singleton instance of {@code StockSplitData}.
     */
    public static StockSplitData getInstance() {
        if (instance == null) {
            instance = new StockSplitData();
        }
        return instance;
    }

    @Override
    public List<StockSplit> parse(String response) {
        List<StockSplit> splits = new ArrayList<>();
        JSONObject oResponse = null;

        try {
            oResponse = new JSONObject(response)
                .getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0);

            if (oResponse.has("events") && oResponse.getJSONObject("events").has("splits")) {
                JSONObject oSplits = oResponse.getJSONObject("events").getJSONObject("splits");

                for (String timestamp : oSplits.keySet()) {
                    JSONObject splitInfo = oSplits.getJSONObject(timestamp);

                    String splitRatio = splitInfo.getString("splitRatio");
                    Date date = DateUtils.fromTimestamp(timestamp);

                    splits.add(new StockSplit(splitRatio, date));
                }
            }
            this.logger.info("Stock split data parsed successfully.");
        } catch (Exception e) {
            this.logger.warn("Error parsing the API response.", e);
        } finally {
            if (oResponse != null) oResponse.clear();
        }
        return splits;
    }
}

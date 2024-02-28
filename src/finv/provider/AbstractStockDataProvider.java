package finv.provider;

import finv.Stock;
import finv.util.CrumbYahoo;
import finv.util.LogConfig;
import finv.util.RequestHttp;
import finv.util.UrlBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractStockDataProvider implements StockDataProvider {

    private static final Logger logger = LogConfig.getLogger();
    protected final Stock stock;

    public AbstractStockDataProvider(Stock stock) {
        this.stock = stock;
        LogConfig.configure();
    }

    /**
     * Fetches stock data from the API.
     *
     * @return The stock data fetched from the API.
     */
    @Override
    public Stock fetchData() {
        try {
            String url = UrlBuilder.from(getApiUrl())
                    .addParameter(getRequestParameters())
                    .build();

            logger.info("Fetching stock data from the API: " + url);
            Map<String, String> headers = new HashMap<String, String>() {{
                put("Cookie", CrumbYahoo.getCookie());
                put("Content-Type", "application/json");
                put("Accept", "*/*");
                put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            }};
            StringBuilder response = RequestHttp.fromURL(url)
                    .method("GET")
                    .headers(headers)
                    .get();

            parseApiResponse(response.toString());
            return stock;
        } catch (Exception e) {
            logger.warning("Error fetching stock data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the URL of the API.
     *
     * @return the URL of the API
     */
    protected abstract String getApiUrl();

    /**
     * Retrieves the request parameters for the Java function.
     *
     * @return a map containing the request parameters as key-value pairs
     */
    protected abstract Map<String, String> getRequestParameters();

    /**
     * Parses the response from the API.
     *
     * @param response the response string to be parsed
     */
    protected abstract void parseApiResponse(String response);
}

package br.dev.rplus.finv.provider;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.util.CrumbYahoo;
import br.dev.rplus.finv.util.RequestHttp;
import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.cup.net.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStockDataProvider implements StockDataProvider {

    protected final Stock stock;

    protected AbstractStockDataProvider(Stock stock) {
        this.stock = stock;
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

            LoggerCup.trace("Fetching stock data from the API: %s", url);
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
            LoggerCup.warn("Error fetching stock data .", e);
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

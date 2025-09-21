package br.dev.rplus.finv.provider;

import br.dev.rplus.cup.request.HttpRequester;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.cup.request.UrlBuilder;
import br.dev.rplus.finv.enums.RequestParams;
import br.dev.rplus.finv.provider.yahoo.CrumbYahoo;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract base class for fetching stock data from external APIs.
 * Provides a template for building API requests, fetching data, and parsing responses.
 * Subclasses should implement the abstract methods to specify the API URL, request parameters, and response parsing logic.
 */
public abstract class AbstractStockDataProvider implements StockDataProvider {

    /**
     * The {@link Stock} object to be populated with the fetched data.
     */
    private final Stock stock;

    protected final Logger logger = Logger.getInstance();

    /**
     * Constructs an {@code AbstractStockDataProvider} with a given {@link Stock} object.
     * This stock object will be populated with the data fetched from the external API.
     *
     * @param stock the {@link Stock} object associated with this data provider.
     */
    protected AbstractStockDataProvider(Stock stock) {
        this.stock = stock;
    }

    @Override
    public Stock getStock() {
        return this.stock;
    }

    @Override
    public void fetchData() {
        try {
            URI url = UrlBuilder.builder()
                .url(getApiUrl())
                .addParameters(getRequestParameters())
                .toURI();
            this.logger.debug("Fetching stock data from the API: %s", url.toString());

            Map<String, String> headers = new HashMap<>() {{
                put("Cookie", CrumbYahoo.getCookie());
                put("Content-Type", "application/json");
                put("Accept", "*/*");
                put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            }};
            StringBuilder response = HttpRequester.builder()
                .url(url)
                .method(HttpRequester.HttpMethod.GET)
                .connectionTimeout(RequestParams.TIMEOUT.get().asInteger())
                .headers(headers)
                .send();

            if (response == null) {
                this.logger.warn("Error fetching stock data. The response is null.");
                return;
            }

            parseApiResponse(response.toString());

        } catch (Exception e) {
            this.logger.warn("Error fetching stock data.", e);
        }
    }

    /**
     * Prepares a request URL by appending the stock's ticker to the given base URL.
     * This method ensures the ticker is URL-encoded.
     *
     * @param url the base URL to which the ticker will be appended.
     * @return the full URL with the ticker appended and properly encoded.
     */
    protected String prepareUrl(String url) {
        return String.format("%s%s", url, URLEncoder.encode(this.getStock().getTicker(), StandardCharsets.UTF_8));
    }

    /**
     * Returns the API URL used to fetch stock data.
     * Subclasses must implement this method to provide the specific URL for the API being queried.
     *
     * @return a string representing the API URL.
     */
    protected abstract String getApiUrl();

    /**
     * Retrieves the request parameters required for the API call.
     * Subclasses must implement this method to return a map of key-value pairs representing the parameters.
     *
     * @return a map of request parameters.
     */
    protected abstract Map<String, String> getRequestParameters();

    /**
     * Parses the response received from the API and populates the associated {@link Stock} object with relevant data.
     * Subclasses must implement this method to extract and set the required information from the API response.
     *
     * @param response a string containing the API response in JSON format.
     */
    protected abstract void parseApiResponse(String response);
}

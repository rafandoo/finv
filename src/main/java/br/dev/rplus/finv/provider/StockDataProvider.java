package br.dev.rplus.finv.provider;

import br.dev.rplus.finv.Stock;

/**
 * Defines the contract for fetching stock data from external APIs.
 * Implementations of this interface should handle the process of building requests,
 * fetching data, and populating a {@link Stock} object with the retrieved information.
 */
public interface StockDataProvider {

    /**
     * Fetches stock data from the external API and populates the associated {@link Stock} object.
     * Implementations should handle API request construction, execution, and response parsing.
     * If an error occurs during the process, it should be logged appropriately.
     */
    void fetchData();

    /**
     * Retrieves the {@link Stock} object associated with the provider.
     * This object is populated with data fetched from the external API.
     *
     * @return the {@link Stock} object containing stock information.
     */
    Stock getStock();
}

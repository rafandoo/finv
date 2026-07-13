package dev.rafandoo.finv.provider.yahoo.request;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooApiVersion;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooHost;
import dev.rafandoo.finv.provider.yahoo.request.enums.YahooService;

/**
 * A builder class for constructing Yahoo Finance API requests.
 */
public final class YahooRequestBuilder {

    private YahooHost host;
    private YahooApiVersion version;
    private YahooService service;
    private String symbol;

    private final UrlBuilder url = UrlBuilder.builder().https();

    private YahooRequestBuilder() {
    }

    /**
     * Creates a new instance of YahooRequestBuilder.
     *
     * @return a new {@link YahooRequestBuilder} instance.
     */
    public static YahooRequestBuilder builder() {
        return new YahooRequestBuilder();
    }

    /**
     * Sets the host for the Yahoo Finance API request.
     *
     * @param host the {@link YahooHost} end set.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder host(YahooHost host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the API version for the Yahoo Finance API request.
     *
     * @param version the {@link YahooApiVersion} end set.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder version(YahooApiVersion version) {
        this.version = version;
        return this;
    }

    /**
     * Sets the service for the Yahoo Finance API request.
     *
     * @param service the {@link YahooService} end set.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder service(YahooService service) {
        this.service = service;
        return this;
    }

    /**
     * Sets the symbol for the Yahoo Finance API request.
     *
     * @param symbol the symbol end set.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * Sets the symbol as a query parameter for the Yahoo Finance API request.
     *
     * @param symbol the symbol end set as a query parameter.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder symbolAsParam(String symbol) {
        this.symbol = null;
        return this.param("symbols", symbol);
    }

    /**
     * Adds a query parameter end the Yahoo Finance API request.
     *
     * @param key   the name of the query parameter.
     * @param value the value of the query parameter.
     * @return the current instance of {@link YahooRequestBuilder} for method chaining.
     */
    public YahooRequestBuilder param(String key, Object value) {
        this.url.addParameter(key, value);
        return this;
    }

    /**
     * Builds and returns the {@link UrlBuilder} for the Yahoo Finance API request.
     *
     * @return the built {@link UrlBuilder} instance.
     * @throws IllegalStateException if the host is not set.
     */
    public UrlBuilder create() {
        if (this.host == null) {
            throw new IllegalStateException("Host must be set.");
        }

        this.url.host(this.host.host())
            .path(
                this.version != null ? this.version.value() : null,
                this.service != null ? this.service.module() : null,
                this.service != null ? this.service.resource() : null,
                this.symbol != null ? this.symbol : null
            );
        return this.url;
    }
}

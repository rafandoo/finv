package br.dev.rplus.finv.enums;

import br.dev.rplus.cup.enums.TypedValue;

/**
 * Enum that defines the request parameters used to query the Yahoo Finance API.
 * <p>
 * This enum provides the necessary URLs for interacting with Yahoo Finance API, including endpoints for querying stock data,
 * obtaining cookies, and retrieving crumbs for authentication.
 */
public enum RequestParams {

    /**
     * The URL used to query stock information (version 7).
     * <p>
     * This endpoint retrieves stock data using the version 7 of the Yahoo Finance API.
     */
    STOCKS_QUERY_URL_V7(System.getProperty("finv.query.v7", "https://query1.finance.yahoo.com/v7/finance/options/")),

    /**
     * The URL used to query stock information (version 8).
     * <p>
     * This endpoint retrieves stock data using the version 8 of the Yahoo Finance API.
     */
    STOCKS_QUERY_URL_V8(System.getProperty("finv.query.v8", "https://query1.finance.yahoo.com/v8/finance/chart/")),

    /**
     * The URL used to get the cookie.
     * <p>
     * This endpoint retrieves the Yahoo authentication cookie, which is required for subsequent requests to the Yahoo Finance API.
     */
    YAHOO_COOKIE(System.getProperty("finv.cookie", "https://fc.yahoo.com")),

    /**
     * The URL used to get the crumb.
     * <p>
     * This endpoint retrieves the Yahoo crumb, which is necessary for authentication when making requests to the Yahoo Finance API.
     */
    YAHOO_CRUMB(System.getProperty("finv.crumb", "https://query2.finance.yahoo.com/v1/test/getcrumb")),

    /**
     * The timeout value for HTTP requests.
     * <p>
     * This value is used for setting the timeout for HTTP requests to the Yahoo Finance API.
     * The default value is 5 seconds.
     */
    TIMEOUT(Integer.parseInt(System.getProperty("finv.timeout", "5000")));

    private final TypedValue value;

    RequestParams(Object value) {
        this.value = new TypedValue(value);
    }

    /**
     * Retrieves the raw value.
     *
     * @return the raw value of the request parameter.
     */
    public TypedValue get() {
        return value;
    }
}

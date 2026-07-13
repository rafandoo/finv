package dev.rafandoo.finv.provider.yahoo.request.enums;

/**
 * Enum representing the different Yahoo Finance hosts.
 */
public enum YahooHost {

    QUERY1("query1.finance.yahoo.com"),
    QUERY2("query2.finance.yahoo.com"),

    FINANCE("finance.yahoo.com"),

    FC("fc.yahoo.com");

    private final String host;

    YahooHost(String host) {
        this.host = host;
    }

    public String host() {
        return this.host;
    }
}

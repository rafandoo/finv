package dev.rafandoo.finv.provider.yahoo.request.enums;

/**
 * Enum representing the different Yahoo Finance services.
 */
public enum YahooService {

    GET_CRUMB("test", "getcrumb"),
    QUOTE_SUMMARY("finance", "quoteSummary"),
    QUOTE("finance", "quote"),
    CHART("finance", "chart");

    private final String module;
    private final String resource;

    YahooService(String module, String resource) {
        this.module = module;
        this.resource = resource;
    }

    public String module() {
        return this.module;
    }

    public String resource() {
        return this.resource;
    }
}

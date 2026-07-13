package dev.rafandoo.finv.provider.yahoo.request.enums;

/**
 * Enum representing the versions of the Yahoo Finance API.
 */
public enum YahooApiVersion {

    V1("v1"),
    V7("v7"),
    V8("v8"),
    V10("v10");

    private final String value;

    YahooApiVersion(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

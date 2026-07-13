package dev.rafandoo.finv.provider.yahoo.request.enums;

/**
 * Enum representing the types of events that can be requested from the Yahoo Finance API.
 */
public enum YahooEvent {

    HISTORY("history"),
    DIVIDEND("div"),
    SPLIT("split");

    private final String value;

    YahooEvent(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

}

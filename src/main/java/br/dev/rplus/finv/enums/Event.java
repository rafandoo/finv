package br.dev.rplus.finv.enums;

import br.dev.rplus.cup.enums.TypedValue;

/**
 * Enum representing different event types in the financial context.
 * <p>
 * Each event corresponds to a specific type of financial data, such as dividends, splits, or historical data.
 */
public enum Event {

    /**
     * Represents a dividend event.
     * <p>
     * This event type is used to track dividend payments associated with a particular stock.
     */
    DIVIDENDS("div"),

    /**
     * Represents a stock split event.
     * <p>
     * This event type is used to track stock splits that affect the stock price and shares outstanding.
     */
    SPLIT("split"),

    /**
     * Represents a historical event.
     * <p>
     * This event type is used for general history or data relating to a stock's past performance.
     */
    HISTORY("history");

    private final TypedValue value;

    Event(Object value) {
        this.value = new TypedValue(value);
    }

    /**
     * Retrieves the name associated with the event type.
     * <p>
     * The name is represented by the value of the event as a string (e.g., "div", "split").
     *
     * @return The name of the event type as a string.
     */
    public String getName() {
        return this.value.asString();
    }
}

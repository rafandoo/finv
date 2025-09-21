package br.dev.rplus.finv.enums;

import br.dev.rplus.cup.enums.TypedValue;

/**
 * Enum representing various frequency types.
 * <p>
 * This enum provides different frequency values that are typically used for financial data retrieval, such as daily, weekly, or yearly data.
 */
public enum Frequency {

    /**
     * Daily frequency.
     * <p>
     * This frequency type represents daily data retrieval.
     */
    DAILY("1d"),

    /**
     * Weekly frequency.
     * <p>
     * This frequency type represents data retrieval at weekly intervals.
     */
    WEEKLY("5d"),

    /**
     * Monthly frequency.
     * <p>
     * This frequency type represents data retrieval at monthly intervals.
     */
    MONTHLY("1mo"),

    /**
     * Quarterly frequency.
     * <p>
     * This frequency type represents data retrieval at quarterly intervals.
     */
    QUARTERLY("3mo"),

    /**
     * Semi-annual frequency.
     * <p>
     * This frequency type represents data retrieval at semi-annual intervals.
     */
    SEMI_ANNUAL("6mo"),

    /**
     * Yearly frequency.
     * <p>
     * This frequency type represents data retrieval at yearly intervals.
     */
    YEARLY("1y"),

    /**
     * Biennial frequency.
     * <p>
     * This frequency type represents data retrieval at biennial intervals (every two years).
     */
    BIENNIAL("2y"),

    /**
     * Quinquennial frequency.
     * <p>
     * This frequency type represents data retrieval at quinquennial intervals (every five years).
     */
    QUINQUENNIAL("5y"),

    /**
     * Decennial frequency.
     * <p>
     * This frequency type represents data retrieval at decennial intervals (every ten years).
     */
    DECENNIAL("10y"),

    /**
     * Year-to-date frequency.
     * <p>
     * This frequency type represents data retrieval for the current year up to the current date.
     */
    YEAR_TO_DATE("ytd"),

    /**
     * Maximum frequency.
     * <p>
     * This frequency type represents the maximum available data range.
     */
    MAXIMUM("max");

    private final TypedValue value;

    Frequency(Object value) {
        this.value = new TypedValue(value);
    }

    /**
     * Retrieves the name associated with the frequency type.
     * <p>
     * The name is represented by the value of the frequency as a string (e.g., "1d", "5d", "1mo").
     *
     * @return The name of the frequency type as a string.
     */
    public String getName() {
        return this.value.asString();
    }
}

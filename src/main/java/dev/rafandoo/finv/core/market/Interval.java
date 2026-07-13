package dev.rafandoo.finv.core.market;

import lombok.Getter;

/**
 * Represents the intervals for which market data can be retrieved.
 */
@Getter
public enum Interval {

    DAILY("1d"),
    WEEKLY("5d"),
    MONTHLY("1mo"),
    QUARTERLY("3mo"),
    SEMI_ANNUAL("6mo"),
    YEARLY("1y"),
    BIENNIAL("2y"),
    QUINQUENNIAL("5y"),
    DECENNIAL("10y"),
    YEAR_TO_DATE("ytd"),
    MAXIMUM("max")
    ;

    private final String value;

    Interval(String value) {
        this.value = value;
    }

}

package br.dev.rplus.finv.enums;

import br.dev.rplus.cup.enums.TypedValue;
import br.dev.rplus.finv.stats.*;

/**
 * Enum for stock statistics (stats).
 * <p>
 * This enum represents various statistical calculations that can be performed on stock data. Each constant
 * corresponds to a specific stock statistic and is associated with a calculator for computing that statistic.
 *
 * @see StatisticsCalculator
 */
public enum Stats {

    /**
     * The average closing price of a stock.
     * <p>
     * This statistic calculates the average closing price of a stock over a given period.
     */
    AVERAGE_CLOSE_PRICE(new AverageClosePriceCalculator()),

    /**
     * The total dividends paid by a stock.
     * <p>
     * This statistic calculates the total amount of dividends paid by a stock over a given period.
     */
    TOTAL_DIVIDENDS(new TotalDividendsCalculator()),

    /**
     * The maximum closing price of a stock.
     * <p>
     * This statistic calculates the maximum closing price of a stock over a given period.
     */
    MAX_CLOSING_PRICE(new MaxClosingPriceCalculator()),

    /**
     * The minimum closing price of a stock.
     * <p>
     * This statistic calculates the minimum closing price of a stock over a given period.
     */
    MIN_CLOSING_PRICE(new MinClosingPriceCalculator()),

    /**
     * The maximum dividend yield of a stock.
     * <p>
     * This statistic calculates the maximum dividend yield of a stock over a given period.
     */
    DIVIDEND_YIELD(new DividendYieldCalculator());

    private final TypedValue value;

    Stats(StatisticsCalculator calculator) {
        this.value = new TypedValue(calculator);
    }

    /**
     * Retrieves the {@link StatisticsCalculator} object associated with this enum instance.
     * <p>
     * This method returns the calculator that is used to compute the statistic represented by this enum constant.
     *
     * @return the {@link StatisticsCalculator} object.
     */
    public StatisticsCalculator getCalculator() {
        return this.value.getTypedValue(StatisticsCalculator.class);
    }
}

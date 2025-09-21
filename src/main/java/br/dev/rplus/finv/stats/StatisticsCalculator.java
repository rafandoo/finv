package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;

/**
 * Interface for classes that calculate statistics for a stock.
 * <p>
 * This functional interface is designed for statistics calculators that operate on stock data.
 * Each implementation of this interface will provide a specific calculation for a stock statistic,
 * such as average closing price, total dividends, maximum closing price, etc.
 * </p>
 *
 * @see Stock
 */
@FunctionalInterface
public interface StatisticsCalculator {

    /**
     * Calculates a statistic for a given stock.
     * <p>
     * This method is intended to be implemented by classes that perform specific statistical
     * calculations on stock data. The calculation can vary based on the type of statistic.
     * </p>
     *
     * @param stock the stock for which the statistic is calculated.
     * @return the calculated statistic.
     *         The return value will depend on the type of statistic being calculated,
     *         such as average price, total dividends, etc.
     */
    double calculate(Stock stock);
}

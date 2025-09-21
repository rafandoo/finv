package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.enums.Stats;

/**
 * Class that represents the statistics of a stock and calculates the result using the corresponding stats calculator.
 * <p>
 * This class is used to compute various statistics for a given stock, such as average closing price,
 * dividend yield, maximum closing price, etc. It delegates the calculation to the specific calculator
 * based on the statistic type passed during instantiation.
 * </p>
 *
 * @see Stats
 * @see StatisticsCalculator
 */
public class StockStats {

    private final Stock stock;
    private final Stats stats;

    /**
     * The default constructor.
     * <p>
     * Initializes a new instance of {@link StockStats} with the specified stock and statistic type.
     * </p>
     *
     * @param stock the stock for which the statistics are calculated.
     * @param stats the statistics to be calculated.
     */
    public StockStats(Stock stock, Stats stats) {
        this.stock = stock;
        this.stats = stats;
    }

    /**
     * Calculates the result using the stats calculator associated with the current statistic.
     * <p>
     * The method retrieves the appropriate {@link StatisticsCalculator} from the {@link Stats} enum
     * and calls its {@link StatisticsCalculator#calculate(Stock)} method to compute the statistic.
     * </p>
     *
     * @return the calculated result based on the chosen statistic.
     */
    public double calculate() {
        return stats.getCalculator().calculate(stock);
    }
}

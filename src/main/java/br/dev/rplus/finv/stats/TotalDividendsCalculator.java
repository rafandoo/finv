package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockDividend;

/**
 * The TotalDividendsCalculator class calculates the sum of all dividend amounts for a given stock.
 * <p>
 * This class is used to calculate the total amount of dividends paid by a stock over its history.
 * It sums the dividend amounts from the stock's dividend history.
 * </p>
 */
public class TotalDividendsCalculator implements StatisticsCalculator {

    /**
     * The default constructor.
     * <p>
     * Initializes a new instance of {@link TotalDividendsCalculator}.
     * </p>
     */
    public TotalDividendsCalculator() {}

    @Override
    public double calculate(Stock stock) {
        if (stock.getDividendHistory() == null || stock.getDividendHistory().isEmpty()) {
            return 0;
        }
        return stock.getDividendHistory().stream()
            .mapToDouble(StockDividend::amount)
            .sum();
    }
}

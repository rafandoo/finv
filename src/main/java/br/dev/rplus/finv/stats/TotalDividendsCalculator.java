package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockDividend;

public class TotalDividendsCalculator implements StatisticsCalculator {

    /**
     * Calculates the sum of all dividend amounts for a given stock.
     *
     * @param  stock  the stock object for which the dividend sum is calculated
     * @return        the sum of all dividend amounts for the given stock,
     *                or 0 if the dividend history is null or empty
     */
    @Override
    public double calculate(Stock stock) {
        if (stock.getDividendHistory() == null || stock.getDividendHistory().isEmpty()) {
            return 0;
        }
        return stock.getDividendHistory().stream()
                .mapToDouble(StockDividend::getAmount)
                .sum();
    }
}

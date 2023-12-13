package finv.stats;

import finv.Stock;

public class StockStats {
    private final Stock stock;
    private final Stats stats;

    public StockStats(Stock stock, Stats stats) {
        this.stock = stock;
        this.stats = stats;
    }

    /**
     * Calculates the result using the stats calculator.
     *
     * @return  the calculated result
     */
    public double calculate() {
        return stats.getCalculator().calculate(stock);
    }
}

package br.dev.rplus.finv.stats;

public enum Stats {

    AVERAGE_CLOSE_PRICE(new AverageClosePriceCalculator()),
    TOTAL_DIVIDENDS(new TotalDividendsCalculator()),
    MAX_CLOSING_PRICE(new MaxClosingPriceCalculator()),
    MIN_CLOSING_PRICE(new MinClosingPriceCalculator()),
    DIVIDEND_YIELD(new DividendYieldCalculator());

    private final StatisticsCalculator calculator;

    Stats(StatisticsCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Retrieves the StatisticsCalculator object associated with this instance.
     *
     * @return          The StatisticsCalculator object.
     */
    public StatisticsCalculator getCalculator() {
        return this.calculator;
    }
}

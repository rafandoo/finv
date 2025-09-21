package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockHistoricalQuote;

import java.util.List;

/**
 * AverageClosePriceCalculator is a class that calculates the average closing price of a stock based on its historical quotes.
 * <p>
 * This calculator sums the closing prices of all historical quotes for a given stock and divides it by the number
 * of quotes available, providing the average closing price over the given period.
 * </p>
 *
 * @see Stock
 * @see StockHistoricalQuote
 */
public class AverageClosePriceCalculator implements StatisticsCalculator {

    /**
     * The default constructor.
     */
    public AverageClosePriceCalculator() {}

    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> quotes = stock.getQuoteHistory();

        // Return 0 if there are no quotes
        if (quotes == null || quotes.isEmpty()) {
            return 0;
        }

        double sum = 0;

        // Sum the closing prices of all quotes
        for (StockHistoricalQuote quote : quotes) {
            sum += quote.close();
        }

        // Return the average closing price
        return sum / quotes.size();
    }
}

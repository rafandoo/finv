package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockHistoricalQuote;

import java.util.List;

/**
 * MaxClosingPriceCalculator is a class that calculates the maximum closing price of a stock based on its historical quotes.
 * <p>
 * The maximum closing price is determined by iterating through the stock's historical quotes and finding the highest closing price.
 * </p>
 *
 * @see Stock
 * @see StockHistoricalQuote
 */
public class MaxClosingPriceCalculator implements StatisticsCalculator {

    /**
     * The default constructor.
     */
    public MaxClosingPriceCalculator() {}

    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> historicalQuotes = stock.getQuoteHistory();

        // Return 0 if no historical quotes are available
        if (historicalQuotes == null || historicalQuotes.isEmpty()) {
            return 0.0;
        }

        double maxClosingPrice = Double.MIN_VALUE;

        // Iterate through historical quotes to find the maximum closing price
        for (StockHistoricalQuote quote : historicalQuotes) {
            maxClosingPrice = Math.max(maxClosingPrice, quote.close());
        }

        return maxClosingPrice;
    }
}

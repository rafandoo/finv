package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockHistoricalQuote;

import java.util.List;

/**
 * MinClosingPriceCalculator is a class that calculates the minimum closing price of a stock based on its historical quotes.
 * <p>
 * The minimum closing price is determined by iterating through the stock's historical quotes and finding the lowest closing price.
 * </p>
 *
 * @see Stock
 * @see StockHistoricalQuote
 */
public class MinClosingPriceCalculator implements StatisticsCalculator {

    /**
     * The default constructor.
     */
    public MinClosingPriceCalculator() {}

    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> historicalQuotes = stock.getQuoteHistory();

        // Return 0 if no historical quotes are available
        if (historicalQuotes == null || historicalQuotes.isEmpty()) {
            return 0.0;
        }

        double minClosingPrice = Double.MAX_VALUE;

        // Iterate through historical quotes to find the minimum closing price
        for (StockHistoricalQuote quote : historicalQuotes) {
            minClosingPrice = Math.min(minClosingPrice, quote.close());
        }

        return minClosingPrice;
    }
}

package finv.stats;

import finv.Stock;
import finv.data.StockHistoricalQuote;

import java.util.List;

public class MaxClosingPriceCalculator implements StatisticsCalculator {

    /**
     * Calculates the maximum closing price from a list of historical quotes for a given stock.
     *
     * @param  stock  the stock object for which the maximum closing price is calculated
     * @return        the maximum closing price from the list of historical quotes, or 0.0 if no historical quotes are available
     */
    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> historicalQuotes = stock.getQuoteHistory();

        if (historicalQuotes == null || historicalQuotes.isEmpty()) {
            return 0.0; // Return 0 if no historical quotes are available
        }

        double maxClosingPrice = Double.MIN_VALUE;

        for (StockHistoricalQuote quote : historicalQuotes) {
            maxClosingPrice = Math.max(maxClosingPrice, quote.getClose());
        }

        return maxClosingPrice;
    }
}

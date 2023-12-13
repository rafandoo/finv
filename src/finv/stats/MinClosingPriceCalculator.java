package finv.stats;

import finv.Stock;
import finv.data.StockHistoricalQuote;

import java.util.List;

public class MinClosingPriceCalculator implements StatisticsCalculator {

    /**
     * Calculates the minimum closing price from a list of historical quotes for a given stock.
     *
     * @param  stock  the stock object for which the minimum closing price is calculated
     * @return        the minimum closing price of the stock, or 0.0 if no historical quotes are available
     */
    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> historicalQuotes = stock.getQuoteHistory();

        if (historicalQuotes == null || historicalQuotes.isEmpty()) {
            return 0.0; // Return 0 if no historical quotes are available
        }

        double minClosingPrice = Double.MAX_VALUE;

        for (StockHistoricalQuote quote : historicalQuotes) {
            minClosingPrice = Math.min(minClosingPrice, quote.getClose());
        }

        return minClosingPrice;
    }
}

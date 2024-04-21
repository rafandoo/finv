package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockHistoricalQuote;

import java.util.List;

public class AverageClosePriceCalculator implements StatisticsCalculator {

    /**
     * Calculates the average closing price of a stock based on its historical quotes.
     *
     * @param  stock  the stock for which to calculate the average closing price
     * @return        the average closing price of the stock
     */
    @Override
    public double calculate(Stock stock) {
        List<StockHistoricalQuote> quotes = stock.getQuoteHistory();

        if (quotes == null || quotes.isEmpty()) {
            return 0;
        }

        double sum = 0;

        for (StockHistoricalQuote quote : quotes) {
            sum += quote.getClose();
        }

        return sum / quotes.size();
    }
}

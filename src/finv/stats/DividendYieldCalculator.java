package finv.stats;

import finv.Stock;
import finv.data.StockDividend;
import finv.data.StockQuote;

import java.util.List;

public class DividendYieldCalculator implements StatisticsCalculator {

    /**
     * Calculates the dividend yield of a stock based on its current quote.
     *
     * @param  stock  the stock object for which to calculate the dividend yield
     * @return        the dividend yield as a percentage
     */
    @Override
    public double calculate(Stock stock) {
        StockQuote currentQuote = stock.getQuote();

        if (currentQuote == null || currentQuote.getPrice() == 0.0) {
            return 0.0; // Return 0 if current quote is not available or price is zero
        }

        double annualDividends = calculateAnnualDividends(stock);
        double currentPrice = currentQuote.getPrice();

        return (annualDividends / currentPrice) * 100;
    }

    /**
     * Calculates the annual dividends for a given stock.
     *
     * @param  stock  the stock for which the dividends are calculated
     * @return        the annual dividends for the stock
     */
    private double calculateAnnualDividends(Stock stock) {
        List<StockDividend> dividendHistory = stock.getDividendHistory();

        if (dividendHistory == null || dividendHistory.isEmpty()) {
            return 0.0; // Return 0 if no dividend history is available
        }

        double totalDividends = 0.0;

        for (StockDividend dividend : dividendHistory) {
            totalDividends += dividend.getAmount();
        }

        return totalDividends * (365.0 / stock.getQuote().getPreviousClose());
    }
}

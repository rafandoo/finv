package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.StockDividend;
import br.dev.rplus.finv.data.StockQuote;

import java.util.List;

/**
 * DividendYieldCalculator is a class that calculates the dividend yield of a stock based on its current quote and dividend history.
 * <p>
 * The dividend yield is calculated by dividing the annual dividends by the current stock price and multiplying the result by 100 to get the percentage.
 * </p>
 *
 * @see Stock
 * @see StockQuote
 * @see StockDividend
 */
public class DividendYieldCalculator implements StatisticsCalculator {

    /**
     * The default constructor.
     */
    public DividendYieldCalculator() {}

    @Override
    public double calculate(Stock stock) {
        StockQuote currentQuote = stock.getQuote();

        // Return 0 if current quote is not available or price is zero
        if (currentQuote == null || currentQuote.price() == 0.0) {
            return 0.0;
        }

        double annualDividends = calculateAnnualDividends(stock);
        double currentPrice = currentQuote.price();

        return (annualDividends / currentPrice) * 100;
    }

    /**
     * Calculates the annual dividends for a given stock by summing up the dividends from the dividend history.
     * <p>
     * The total dividends are then multiplied by a factor to annualize them based on the stock's previous close.
     * </p>
     *
     * @param stock the stock for which the annual dividends are calculated.
     * @return the total annual dividends. Returns 0 if no dividend history is available.
     */
    private double calculateAnnualDividends(Stock stock) {
        List<StockDividend> dividendHistory = stock.getDividendHistory();

        // Return 0 if no dividend history is available
        if (dividendHistory == null || dividendHistory.isEmpty()) {
            return 0.0;
        }

        double totalDividends = 0.0;

        // Sum up all dividends in the history
        for (StockDividend dividend : dividendHistory) {
            totalDividends += dividend.amount();
        }

        // Annualize the total dividends based on the previous close price of the stock
        return totalDividends * (365.0 / stock.getQuote().previousClose());
    }
}

package finv;

import finv.data.StockDividend;
import finv.data.StockHistoricalQuote;
import finv.data.StockQuote;
import finv.data.StockSplit;

import java.lang.reflect.Field;
import java.util.List;

public class Stock {

    private final String ticker;
    private String name;
    private String currency;
    private String stockExchange;
    private String quoteType;

    private StockQuote quote;
    private List<StockHistoricalQuote> quoteHistory;
    private List<StockDividend> dividendHistory;
    private List<StockSplit> splitHistory;

    public Stock(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public StockQuote getQuote() {
        return quote;
    }

    public void setQuote(StockQuote quote) {
        this.quote = quote;
    }

    public List<StockHistoricalQuote> getQuoteHistory() {
        return quoteHistory;
    }

    public void setQuoteHistory(List<StockHistoricalQuote> quoteHistory) {
        this.quoteHistory = quoteHistory;
    }

    public List<StockDividend> getDividendHistory() {
        return dividendHistory;
    }

    public void setDividendHistory(List<StockDividend> dividendHistory) {
        this.dividendHistory = dividendHistory;
    }

    public List<StockSplit> getSplitHistory() {
        return splitHistory;
    }

    public void setSplitHistory(List<StockSplit> splitHistory) {
        this.splitHistory = splitHistory;
    }

    public void print() {
        System.out.println("** " + this.ticker + " **\n");
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                System.out.println(field.getName() + ": " + field.get(this));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                System.out.println("Error " + field.getName() + ": " + ex.getMessage());
            }
        }
        System.out.println("\n");
    }
}

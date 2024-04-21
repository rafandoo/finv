package br.dev.rplus.finv;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.data.StockDividend;
import br.dev.rplus.finv.data.StockHistoricalQuote;
import br.dev.rplus.finv.data.StockQuote;
import br.dev.rplus.finv.data.StockSplit;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        this.quoteHistory = new ArrayList<>(quoteHistory);
    }

    public List<StockDividend> getDividendHistory() {
        return dividendHistory;
    }

    public void setDividendHistory(List<StockDividend> dividendHistory) {
        this.dividendHistory = new ArrayList<>(dividendHistory);
    }

    public List<StockSplit> getSplitHistory() {
        return splitHistory;
    }

    public void setSplitHistory(List<StockSplit> splitHistory) {
        this.splitHistory = new ArrayList<>(splitHistory);
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        sb.append("** ").append(this.ticker).append(" **\n");
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                sb.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LoggerCup.error("Error while getting field: %s", field.getName(), ex);
            }
        }
        System.out.println(sb);
    }
}

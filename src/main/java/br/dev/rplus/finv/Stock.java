package br.dev.rplus.finv;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.finv.data.StockDividend;
import br.dev.rplus.finv.data.StockHistoricalQuote;
import br.dev.rplus.finv.data.StockQuote;
import br.dev.rplus.finv.data.StockSplit;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Stock is a class that represents a stock, containing information about the stock itself and its historical data.
 */
@Getter
public class Stock {

    private final String ticker;

    @Setter
    private String name;

    @Setter
    private String currency;

    @Setter
    private String market;

    @Setter
    private String marketState;

    @Setter
    private String quoteType;

    @Setter
    private String region;

    @Setter
    private String exchangeTimezone;

    @Setter
    private String quoteSource;

    @Setter
    private StockQuote quote;

    @Setter
    private List<StockHistoricalQuote> quoteHistory;

    @Setter
    private List<StockDividend> dividendHistory;

    @Setter
    private List<StockSplit> splitHistory;

    /**
     * Constructor for the Stock class.
     *
     * @param ticker the stock ticker.
     */
    public Stock(String ticker) {
        this.ticker = ticker;
    }

    /**
     * Creates a string representation of the stock.
     *
     * @param console if true, the string will be printed to the console.
     * @return the string representation of the stock.
     */
    public StringBuilder print(boolean console) {
        StringBuilder sb = new StringBuilder();
        sb.append("** ").append(this.ticker).append(" **\n");
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                sb.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getInstance().error("Error while getting field: %s", field.getName(), ex);
            }
        }
        if (console) {
            System.out.println(sb);
        }
        return sb;
    }
}

package br.dev.rplus.finv.data;

import java.util.Date;

/**
 * StockHistoricalQuote is a class that represents a historical stock quote.
 * A historical stock quote is a snapshot of a stock's price and other relevant information at a specific point in time in the past.
 *
 * @param date the date of the quote.
 * @param open the opening price of the day.
 * @param close the closing price of the day.
 * @param adjClose the adjusted closing price of the day.
 * @param low the lowest price of the day.
 * @param high the highest price of the day.
 * @param volume the volume of the day.
 */
public record StockHistoricalQuote(
        Date date,
        double open,
        double close,
        double adjClose,
        double low,
        double high,
        Long volume
) {

    @Override
    public String toString() {
        return "StockHistoricalQuote: {" +
                "date: " + date +
                ", open: " + open +
                ", close: " + close +
                ", adjClose: " + adjClose +
                ", low: " + low +
                ", high: " + high +
                ", volume: " + volume +
                '}';
    }
}

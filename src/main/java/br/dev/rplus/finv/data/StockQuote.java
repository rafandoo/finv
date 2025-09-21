package br.dev.rplus.finv.data;

import java.util.Date;

/**
 * StockQuote is a class that represents a stock quote.
 * A stock quote is a snapshot of a stock's price and other relevant information at a specific point in time.
 *
 * @param marketTime the market time of the quote.
 * @param price the current price of the stock.
 * @param change the change in price since the previous quote.
 * @param open the opening price of the day.
 * @param previousClose the previous closing price of the day.
 * @param low the lowest price of the day.
 * @param high the highest price of the day.
 * @param volume the volume of the day.
 * @param bid the bid price of the stock.
 * @param ask the ask price of the stock.
 */
public record StockQuote(
        Date marketTime,
        double price,
        double change,
        double open,
        double previousClose,
        double low,
        double high,
        Long volume,
        double bid,
        double ask
) {

    @Override
    public String toString() {
        return "StockQuote: {" +
                "marketTime: " + marketTime +
                ", price: " + price +
                ", change: " + change +
                ", open: " + open +
                ", previousClose: " + previousClose +
                ", low: " + low +
                ", high: " + high +
                ", volume: " + volume +
                ", bid: " + bid +
                ", ask: " + ask +
                '}';
    }
}

package br.dev.rplus.finv.data;

import java.util.Date;

/**
 * StockDividend is a class that represents a stock dividend event.
 * A stock dividend is a corporate action that distributes additional shares of a company to its shareholders.
 * This is done to reward shareholders and increase the liquidity of the shares.
 *
 * @param amount the amount of the dividend.
 * @param date the date of the dividend.
 */
public record StockDividend(
        double amount,
        Date date
) {

    @Override
    public String toString() {
        return "StockDividend: {" +
                "amount: " + amount +
                ", date: " + date +
                '}';
    }
}

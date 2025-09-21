package br.dev.rplus.finv.data;

import java.util.Date;

/**
 * StockSplit is a class that represents a stock split event.
 * A stock split is a corporate action that divides the existing shares of a company into multiple shares.
 * This is done to increase the liquidity of the shares and make them more affordable to investors.
 *
 * @param splitRatio the ratio of the split.
 * @param date the date of the split.
 */
public record StockSplit(
        String splitRatio,
        Date date
) {

    @Override
    public String toString() {
        return "StockSplit: {" +
                "splitRatio: '" + splitRatio + '\'' +
                ", date: " + date +
                '}';
    }
}

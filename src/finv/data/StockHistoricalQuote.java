package finv.data;

import java.util.Date;

public class StockHistoricalQuote {

    private Date date;
    private double open;
    private double low;
    private double close;
    private Long volume;
    private double high;
    private double adjClose;

    public StockHistoricalQuote() {
    }

    public StockHistoricalQuote(Date date, double open, double low, double close, Long volume, double high, double adjClose) {
        this.date = date;
        this.open = open;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.high = high;
        this.adjClose = adjClose;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    @Override
    public String toString() {
        return "{" +
                "date=" + date +
                ", open=" + open +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", high=" + high +
                ", adjClose=" + adjClose +
                "}";
    }
}

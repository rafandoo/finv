package br.dev.rplus.finv.data;

public class StockQuote {
    private double price;
    private double change;
    private double open;
    private double previousClose;
    private Long volume;
    private double bid;
    private double ask;

    public StockQuote() {
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }


    @Override
    public String toString() {
        return "["
                + "Price: " + price
                + ", Bid: " + bid
                + ", Ask: " + ask
                + ", Change: " + change
                + ", Open: " + open
                + ", Previous Close: " + previousClose
                + ", Volume: " + volume
                + "]";
    }
}

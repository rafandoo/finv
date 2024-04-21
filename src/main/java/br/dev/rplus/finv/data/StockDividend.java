package br.dev.rplus.finv.data;

import java.util.Date;

public class StockDividend {
    private double amount;
    private Date date;

    public StockDividend() {
    }

    public StockDividend(double amount, Date date) {
        this.amount = amount;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
                "amount=" + amount +
                ", date=" + date +
                "}";
    }
}

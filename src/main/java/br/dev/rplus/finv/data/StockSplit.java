package br.dev.rplus.finv.data;

import java.util.Date;

public class StockSplit {
    private String splitRatio;
    private Date date;

    public StockSplit() {
    }

    public StockSplit(String splitRatio, Date date) {
        this.splitRatio = splitRatio;
        this.date = date;
    }

    public String getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(String splitRatio) {
        this.splitRatio = splitRatio;
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
                "splitRatio=" + splitRatio +
                ", date=" + date +
                "}";
    }
}

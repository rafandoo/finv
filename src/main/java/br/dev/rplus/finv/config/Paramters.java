package br.dev.rplus.finv.config;

public abstract class Paramters {

    public static final String STOCKS_QUERY_URL_V7 = System.getProperty("finv.query.v7", "https://query1.finance.yahoo.com/v7/finance/options/");
    public static final String STOCKS_QUERY_URL_V8 = System.getProperty("finv.query.v8", "https://query1.finance.yahoo.com/v8/finance/chart/");
    public static final String YAHOO_COOKIE = System.getProperty("finv.cookie", "https://fc.yahoo.com");
    public static final String YAHOO_CRUMB = System.getProperty("finv.crumb", "https://query2.finance.yahoo.com/v1/test/getcrumb");
    public static final String LOG_LEVEL = System.getProperty("finv.log.level", "INFO");
}

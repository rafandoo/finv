package br.dev.rplus.finv;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.config.Paramters;
import br.dev.rplus.finv.data.Event;
import br.dev.rplus.finv.data.Frequency;
import br.dev.rplus.finv.export.ExportData;
import br.dev.rplus.finv.export.ExportType;
import br.dev.rplus.finv.provider.*;
import br.dev.rplus.finv.stats.Stats;
import br.dev.rplus.finv.stats.StockStats;
import br.dev.rplus.cup.others.DateFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The FInv library (Finance Invest) is a software library that provides functions for
 * financial analysis and investments in the Brazilian stock market, being a simple and
 * powerful library that facilitates the development of investment applications, providing
 * functions ready to consult, calculate and visualize financial indicators.
 *
 * @author Rafael Camargo
 * @version 1.0.2
 */
public class Finv {

    /**
     * Private constructor to prevent instantiation.
     */
    private Finv() {
        LoggerCup.init("br.dev.rplus.finv", Paramters.LOG_LEVEL);
    }

    /**
     * Retrieves a stock using the provided ticker symbol.
     *
     * @param ticker the ticker symbol of the stock to retrieve
     * @return the retrieved stock
     */
    public static Stock get(String ticker) {
        return getQuotes(getStock(ticker));
    }

    /**
     * Retrieves a stock based on the given ticker and event.
     *
     * @param ticker the ticker symbol of the stock
     * @param event  the event associated with the stock
     * @return the retrieved stock object
     */
    public static Stock get(String ticker, Event event) {
        return get(ticker, Collections.singletonList(event));
    }

    /**
     * Retrieves a stock based on the ticker, event, and frequency.
     *
     * @param ticker    the ticker symbol of the stock
     * @param event     the event associated with the stock
     * @param frequency the frequency of the stock data
     * @return the stock object
     */
    public static Stock get(String ticker, Event event, Frequency frequency) {
        return get(ticker, Collections.singletonList(event), frequency);
    }

    /**
     * Retrieves a stock based on the specified ticker, event, start date, and end date.
     *
     * @param ticker       the ticker symbol of the stock
     * @param event        the event to filter the stock data
     * @param startDateStr the start date of the stock data in string format
     * @param endDateStr   the end date of the stock data in string format
     * @return the retrieved stock
     */
    public static Stock get(String ticker, Event event, String startDateStr, String endDateStr) {
        return get(ticker, Collections.singletonList(event), startDateStr, endDateStr, Frequency.DAILY);
    }

    /**
     * Retrieves a Stock object based on the given ticker and list of events.
     *
     * @param ticker the ticker symbol of the stock
     * @param events a list of events related to the stock
     * @return the Stock object representing the stock with the given ticker and events
     */
    public static Stock get(String ticker, List<Event> events) {
        return get(ticker, events, Frequency.DAILY);
    }

    /**
     * Retrieves the stock information for the given ticker within a specified time range.
     *
     * @param ticker    the ticker symbol of the stock
     * @param events    the list of events associated with the stock
     * @param frequency the frequency at which the stock information is obtained
     * @return the stock information for the given ticker
     */
    public static Stock get(String ticker, List<Event> events, Frequency frequency) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);
        String startDateStr = DateFormatter.format(startDate);
        String endDateStr = DateFormatter.format(endDate);
        return get(ticker, events, startDateStr, endDateStr, frequency);
    }

    /**
     * Retrieves a Stock object based on the given ticker symbol, list of events, start date, and end date.
     *
     * @param ticker       the ticker symbol of the stock
     * @param events       a list of events associated with the stock
     * @param startDateStr the start date of the stock data in string format
     * @param endDateStr   the end date of the stock data in string format
     * @return the Stock object retrieved based on the given parameters
     */
    public static Stock get(String ticker, List<Event> events, String startDateStr, String endDateStr) {
        return get(ticker, events, startDateStr, endDateStr, Frequency.DAILY);
    }

    /**
     * Retrieves a stock object with the specified ticker symbol, along with additional events if provided.
     *
     * @param ticker    the ticker symbol of the stock to retrieve
     * @param events    a list of events to include in the stock object (e.g., history, dividends, split)
     * @param startDate the start date of the events range in string format
     * @param endDate   the end date of the events range in string format
     * @param frequency the frequency at which to retrieve the events (e.g., daily, weekly, monthly)
     * @return the stock object with the specified ticker and events
     */
    public static Stock get(String ticker, List<Event> events, String startDate, String endDate, Frequency frequency) {
        Stock stockEvents = get(ticker);
        if (!Objects.requireNonNull(events).isEmpty()) {
            for (Event event : events) {
                if (event == Event.HISTORY) {
                    getHistory(stockEvents, startDate, endDate, frequency);
                } else if (event == Event.DIVIDENDS) {
                    getDividends(stockEvents, startDate, endDate, frequency);
                } else if (event == Event.SPLIT) {
                    getStockSplits(stockEvents, startDate, endDate, frequency);
                }
            }
        }
        return stockEvents;
    }

    /**
     * Retrieves the historical data for a given stock within a specified date range and frequency.
     *
     * @param stock     the stock for which the historical data is to be retrieved
     * @param startDate the start date of the historical data range
     * @param endDate   the end date of the historical data range
     * @param frequency the frequency at which the historical data is to be fetched
     */
    private static void getHistory(Stock stock, String startDate, String endDate, Frequency frequency) {
        StockDataProvider provider = new HistoricalStockData(stock, startDate, endDate, frequency);
        provider.fetchData();
    }

    /**
     * Retrieves the dividends for a given stock within a specified date range and frequency.
     *
     * @param stock     the stock for which to retrieve the dividends
     * @param startDate the start date of the date range
     * @param endDate   the end date of the date range
     * @param frequency the frequency at which to retrieve the dividends
     */
    private static void getDividends(Stock stock, String startDate, String endDate, Frequency frequency) {
        StockDataProvider provider = new StockDividendData(stock, startDate, endDate, frequency);
        provider.fetchData();
    }

    /**
     * Generates the function comment for the given function body.
     *
     * @param stock     the stock for which to get the stock splits
     * @param startDate the start date for the stock splits
     * @param endDate   the end date for the stock splits
     * @param frequency the frequency of the stock splits
     */
    private static void getStockSplits(Stock stock, String startDate, String endDate, Frequency frequency) {
        StockDataProvider provider = new StockSplitData(stock, startDate, endDate, frequency);
        provider.fetchData();
    }

    /**
     * Retrieves stock quotes for a given stock.
     *
     * @param stock the stock for which to retrieve quotes
     * @return the retrieved stock quotes
     */
    private static Stock getQuotes(Stock stock) {
        StockDataProvider provider = new StockQuoteData(stock);
        return provider.fetchData();
    }

    /**
     * Retrieve the stock information for a given ticker symbol.
     *
     * @param ticker the ticker symbol of the stock
     * @return the stock information retrieved
     */
    private static Stock getStock(String ticker) {
        StockDataProvider provider = new StockData(new Stock(ticker));
        return provider.fetchData();
    }

    /**
     * Export the given stock using the specified export type.
     *
     * @param stock      the stock to be exported
     * @param exportType the type of export to be performed
     */
    public static void export(Stock stock, ExportType exportType) {
        ExportData exportData = new ExportData(null);
        exportData.export(stock, exportType);
    }

    /**
     * Calculates the statistics for a given stock using the provided stock and stats objects.
     *
     * @param stock the stock object containing the stock information
     * @param stats the stats object containing the statistical calculations to be performed
     * @return the calculated statistics for the stock
     */
    public static double stats(Stock stock, Stats stats) {
        return new StockStats(stock, stats).calculate();
    }

    /**
     * Generates a list of statistical results based on the provided stock and list of statistics.
     *
     * @param stock     the stock object to perform the calculations on
     * @param statsList the list of statistics to compute
     * @return a list of double values representing the computed statistics
     */
    public static List<Double> stats(Stock stock, List<Stats> statsList) {
        List<Double> results = new ArrayList<>();

        for (Stats stats : statsList) {
            results.add(stats(stock, stats));
        }
        return results;
    }
}

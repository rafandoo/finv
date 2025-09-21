package br.dev.rplus.finv;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.cup.object.export.ExportData;
import br.dev.rplus.cup.object.export.ExportType;
import br.dev.rplus.finv.enums.*;
import br.dev.rplus.finv.provider.*;
import br.dev.rplus.finv.stats.StockStats;
import br.dev.rplus.cup.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The FInv library is a software library that provides functions for
 * financial analysis and investments in the mundial stock market, being a simple and
 * powerful library that facilitates the development of investment applications, providing
 * functions ready to consult, calculate and visualize financial indicators.
 *
 * @author Rafael Camargo
 */
public class Finv {

    /**
     * Private constructor to prevent instantiation.
     */
    private Finv() {
    }

    static {
        init();
    }

    /**
     * Initializes the FInv library.
     */
    private static void init() {
        Logger logger = Logger.getInstance();
        logger.init("br.dev.rplus.finv",
            ConfigParams.LOG_LEVEL.get().asString(),
            ConfigParams.USE_LOG_HANDLER.get().asBoolean(),
            ConfigParams.LOG_FILE.get().asBoolean()
        );
        logger.notice("FInv library initialized.");
        logger.notice("This library is for educational purposes only and should not be used for real investments.");
        logger.notice("Use at your own risk.");
    }

    /**
     * Retrieves a stock using the provided ticker symbol.
     *
     * @param ticker the ticker symbol of the stock to retrieve.
     * @return the retrieved stock.
     */
    public static Stock get(String ticker) {
        return getStock(ticker);
    }

    /**
     * Retrieves a stock based on the given ticker and event.
     *
     * @param ticker the ticker symbol of the stock.
     * @param event  the event associated with the stock.
     * @return the retrieved stock object.
     */
    public static Stock get(String ticker, Event event) {
        return get(ticker, Collections.singletonList(event));
    }

    /**
     * Retrieves a stock based on the ticker, event, and frequency.
     *
     * @param ticker    the ticker symbol of the stock.
     * @param event     the event associated with the stock.
     * @param frequency the frequency of the stock data.
     * @return the stock object.
     */
    public static Stock get(String ticker, Event event, Frequency frequency) {
        return get(ticker, Collections.singletonList(event), frequency);
    }

    /**
     * Retrieves a stock based on the specified ticker, event, start date, and end date.
     *
     * @param ticker       the ticker symbol of the stock.
     * @param event        the event to filter the stock data.
     * @param startDateStr the start date of the stock data in string format.
     * @param endDateStr   the end date of the stock data in string format.
     * @return the retrieved stock.
     */
    public static Stock get(String ticker, Event event, String startDateStr, String endDateStr) {
        return get(ticker, Collections.singletonList(event), startDateStr, endDateStr, Frequency.DAILY);
    }

    /**
     * Retrieves a stock based on the specified ticker, event, start date, and end date.
     *
     * @param ticker       the ticker symbol of the stock.
     * @param event        the event to filter the stock data.
     * @param startDateStr the start date of the stock data in string format.
     * @param endDateStr   the end date of the stock data in string format.
     * @param frequency    the frequency of the stock data.
     * @return the retrieved stock.
     */
    public static Stock get(String ticker, Event event, String startDateStr, String endDateStr, Frequency frequency) {
        return get(ticker, Collections.singletonList(event), startDateStr, endDateStr, frequency);
    }

    /**
     * Retrieves a Stock object based on the given ticker and list of events.
     *
     * @param ticker the ticker symbol of the stock.
     * @param events a list of events related to the stock.
     * @return the Stock object representing the stock with the given ticker and events.
     */
    public static Stock get(String ticker, List<Event> events) {
        return get(ticker, events, Frequency.DAILY);
    }

    /**
     * Retrieves the stock information for the given ticker within a specified time range.
     *
     * @param ticker    the ticker symbol of the stock.
     * @param events    the list of events associated with the stock.
     * @param frequency the frequency at which the stock information is obtained.
     * @return the stock information for the given ticker.
     */
    public static Stock get(String ticker, List<Event> events, Frequency frequency) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);
        var du = new DateUtils();
        String startDateStr = du.format(startDate);
        String endDateStr = du.format(endDate);
        return get(ticker, events, startDateStr, endDateStr, frequency);
    }

    /**
     * Retrieves a Stock object based on the given ticker symbol, list of events, start date, and end date.
     *
     * @param ticker       the ticker symbol of the stock.
     * @param events       a list of events associated with the stock.
     * @param startDateStr the start date of the stock data in string format.
     * @param endDateStr   the end date of the stock data in string format.
     * @return the Stock object retrieved based on the given parameters.
     */
    public static Stock get(String ticker, List<Event> events, String startDateStr, String endDateStr) {
        return get(ticker, events, startDateStr, endDateStr, Frequency.DAILY);
    }

    /**
     * Retrieves a stock object with the specified ticker symbol, along with additional events if provided.
     *
     * @param ticker    the ticker symbol of the stock to retrieve.
     * @param events    a list of events to include in the stock object (e.g., history, dividends, split).
     * @param startDate the start date of the events range in string format.
     * @param endDate   the end date of the events range in string format.
     * @param frequency the frequency at which to retrieve the events (e.g., daily, weekly, monthly).
     * @return the stock object with the specified ticker and events.
     */
    public static Stock get(String ticker, List<Event> events, String startDate, String endDate, Frequency frequency) {
        Stock stockEvents = get(ticker);
        StockDataProvider eventsStockData = new EventsStockData(stockEvents, startDate, endDate, frequency, events);
        eventsStockData.fetchData();
        return stockEvents;
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols.
     *
     * @param tickers the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(Finv::get)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols and event.
     *
     * @param event   the event to filter the stocks.
     * @param tickers the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(Event event, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, event))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, event, and frequency.
     *
     * @param event     the event to filter the stocks.
     * @param frequency the frequency of the stock data.
     * @param tickers   the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(Event event, Frequency frequency, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, event, frequency))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, event, start date, and end date.
     *
     * @param event     the event to filter the stocks.
     * @param startDate the start date of the stock data.
     * @param endDate   the end date of the stock data.
     * @param tickers   the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(Event event, String startDate, String endDate, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, event, startDate, endDate))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, event, start date, end date, and frequency.
     *
     * @param event     the event to filter the stocks.
     * @param startDate the start date of the stock data.
     * @param endDate   the end date of the stock data.
     * @param frequency the frequency of the stock data.
     * @param tickers   the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(Event event, String startDate, String endDate, Frequency frequency, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, event, startDate, endDate, frequency))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols and list of events.
     *
     * @param events   the list of events to filter the stocks.
     * @param tickers  the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(List<Event> events, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, events))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, list of events, and frequency.
     *
     * @param events   the list of events to filter the stocks.
     * @param frequency the frequency of the stock data.
     * @param tickers  the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(List<Event> events, Frequency frequency, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, events, frequency))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, list of events, start date, and end date.
     *
     * @param events    the list of events to filter the stocks.
     * @param startDate the start date of the stock data.
     * @param endDate   the end date of the stock data.
     * @param tickers   the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(List<Event> events, String startDate, String endDate, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, events, startDate, endDate))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves multiple stocks based on the provided ticker symbols, list of events, start date, end date, and frequency.
     *
     * @param events    the list of events to filter the stocks.
     * @param startDate the start date of the stock data.
     * @param endDate   the end date of the stock data.
     * @param frequency the frequency of the stock data.
     * @param tickers   the ticker symbols of the stocks to retrieve.
     * @return a list of Stock objects representing the retrieved stocks.
     */
    public static List<Stock> get(List<Event> events, String startDate, String endDate, Frequency frequency, String... tickers) {
        return Arrays.stream(tickers)
            .distinct()
            .map(ticker -> get(ticker, events, startDate, endDate, frequency))
            .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * Retrieve the stock information for a given ticker symbol.
     *
     * @param ticker the ticker symbol of the stock.
     * @return the stock information retrieved.
     */
    private static Stock getStock(String ticker) {
        StockDataProvider provider = new StockData(new Stock(ticker));
        provider.fetchData();
        return provider.getStock();
    }

    /**
     * Export the given stock using the specified export type.
     *
     * @param stock      the stock to be exported.
     * @param exportType the type of export to be performed.
     */
    public static void export(Stock stock, ExportType exportType) {
        ExportData export = new ExportData();
        export.setExportStrategy(exportType);
        export.export(stock);
    }

    /**
     * Calculates the statistics for a given stock using the provided stock and stats objects.
     *
     * @param stock the stock object containing the stock information.
     * @param stats the stats object containing the statistical calculations to be performed.
     * @return the calculated statistics for the stock.
     */
    public static double stats(Stock stock, Stats stats) {
        return new StockStats(stock, stats).calculate();
    }

    /**
     * Generates a list of statistical results based on the provided stock and list of statistics.
     *
     * @param stock     the stock object to perform the calculations on.
     * @param statsList the list of statistics to compute.
     * @return a list of double values representing the computed statistics.
     */
    public static List<Double> stats(Stock stock, List<Stats> statsList) {
        List<Double> results = new ArrayList<>();
        statsList.forEach(stats -> results.add(stats(stock, stats)));
        return results;
    }
}

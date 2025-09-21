package br.dev.rplus.finv;

import br.dev.rplus.cup.object.export.ExportType;
import br.dev.rplus.finv.enums.Event;
import br.dev.rplus.finv.enums.Stats;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Finv library.
 * <p>
 * This class contains unit tests for the core functionality of the Finv library.
 * The tests cover retrieving stocks, calculating statistics, and exporting data.
 * </p>
 */
class FinvTest {

    /**
     * Test retrieving a stock by its ticker.
     * <p>
     * This test ensures that a stock can be correctly retrieved using its ticker symbol.
     * </p>
     */
    @Test
    void shouldRetrieveStockByTicker() {
        Stock stock = Finv.get("AAPL");
        assertNotNull(stock, "Stock should not be null");
        assertEquals("AAPL", stock.getTicker(), "Ticker should match the input");
    }

    /**
     * Test retrieving a stock with events.
     * <p>
     * This test verifies that when a stock is retrieved with an event (like history),
     * the stock object contains the corresponding event data (e.g., quote history).
     * </p>
     */
    @Test
    void shouldRetrieveStockWithEvents() {
        List<Event> events = Collections.singletonList(Event.HISTORY);
        Stock stock = Finv.get("AAPL", events);
        assertNotNull(stock, "Stock should not be null");
        assertEquals("AAPL", stock.getTicker(), "Ticker should match the input");
        assertNotNull(stock.getQuoteHistory(), "Quote history should not be null when history event is requested");
        assertFalse(stock.getQuoteHistory().isEmpty(), "Quote history should not be empty");
    }

    /**
     * Test retrieving a stock with an invalid ticker.
     * <p>
     * This test ensures that the system handles invalid tickers gracefully.
     * Even for an invalid ticker, a stock object should be returned, but with missing or null data.
     * </p>
     */
    @Test
    void shouldHandleInvalidTickerGracefully() {
        Stock stock = Finv.get("INVALID_TICKER");
        assertNotNull(stock, "Stock object should not be null even for an invalid ticker");
        assertNull(stock.getName(), "Name should be null for an invalid ticker");
    }

    /**
     * Test calculating average close price statistics.
     * <p>
     * This test checks if the average close price is calculated correctly.
     * Since the stock is newly created, the value should default to 0.0.
     * </p>
     */
    @Test
    void shouldCalculateAverageClosePrice() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.AVERAGE_CLOSE_PRICE);
        assertEquals(0.0, result, 0.01, "Average close price should default to 0.0 for a new stock");
    }

    /**
     * Test calculating dividend yield statistics.
     * <p>
     * This test checks if the dividend yield is calculated correctly.
     * Since the stock is new and lacks dividend history, the value should default to 0.0.
     * </p>
     */
    @Test
    void shouldCalculateDividendYield() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.DIVIDEND_YIELD);
        assertEquals(0.0, result, 0.01, "Dividend yield should default to 0.0 for a new stock");
    }

    /**
     * Test calculating multiple statistics at once.
     * <p>
     * This test ensures that multiple statistics can be calculated at once.
     * It verifies that the correct number of results is returned and that each individual result is correct.
     * </p>
     */
    @Test
    void shouldCalculateMultipleStats() {
        Stock stock = new Stock("AAPL");
        List<Double> results = Finv.stats(stock, Arrays.asList(Stats.AVERAGE_CLOSE_PRICE, Stats.DIVIDEND_YIELD));
        assertEquals(2, results.size(), "The number of results should match the number of requested stats");
        assertEquals(0.0, results.get(0), 0.01, "Average close price should default to 0.0");
        assertEquals(0.0, results.get(1), 0.01, "Dividend yield should default to 0.0");
    }

    /**
     * Test exporting a stock to JSON.
     * <p>
     * This test verifies that a stock can be exported to JSON without errors.
     * It ensures that the export functionality is robust and does not throw unexpected exceptions.
     * </p>
     */
    @Test
    void shouldExportStockToJson() {
        Stock stock = new Stock("AAPL");
        assertDoesNotThrow(() -> Finv.export(stock, ExportType.JSON), "Exporting stock to JSON should not throw exceptions");
    }

    /**
     * Test exporting a null stock.
     * <p>
     * This test ensures that the system handles exporting a null stock gracefully.
     * The export function should not throw exceptions when given a null input.
     * </p>
     */
    @Test
    void shouldHandleExportingNullStockGracefully() {
        assertDoesNotThrow(() -> Finv.export(null, ExportType.JSON), "Exporting a null stock should not throw exceptions");
    }

    /**
     * Test retrieving stock with specific event and frequency.
     * <p>
     * This test ensures that when a stock is retrieved with a specific event and frequency,
     * the stock contains the requested event data, and the retrieval respects the given date range.
     * </p>
     */
    @Test
    void shouldRetrieveStockWithEventAndFrequency() {
        Stock stock = Finv.get("AAPL", Event.HISTORY, "2023-01-01", "2023-12-31");
        assertNotNull(stock, "Stock should not be null");
        assertEquals("AAPL", stock.getTicker(), "Ticker should match the input");
        assertNotNull(stock.getQuoteHistory(), "Quote history should not be null");
    }

    /**
     * Test retrieving stock with multiple events.
     * <p>
     * This test verifies that multiple events can be retrieved simultaneously,
     * and that all corresponding data (quote history, dividend history, and split history) is available.
     * </p>
     */
    @Test
    void shouldRetrieveStockWithMultipleEvents() {
        List<Event> events = Arrays.asList(Event.HISTORY, Event.DIVIDENDS, Event.SPLIT);
        Stock stock = Finv.get("AAPL", events);
        assertNotNull(stock, "Stock should not be null");
        assertEquals("AAPL", stock.getTicker(), "Ticker should match the input");
        assertNotNull(stock.getQuoteHistory(), "Quote history should not be null");
        assertNotNull(stock.getDividendHistory(), "Dividend history should not be null");
        assertNotNull(stock.getSplitHistory(), "Split history should not be null");
    }
}

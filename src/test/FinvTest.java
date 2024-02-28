package test;

import finv.Finv;
import finv.Stock;
import finv.data.Event;
import finv.export.ExportType;
import finv.stats.Stats;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Finv test.
 */
public class FinvTest {

    /**
     * Test get stock.
     */
    @Test
    public void testGetStock() {
        Stock stock = Finv.get("AAPL");
        assertNotNull(stock);
        assertEquals("AAPL", stock.getTicker());
    }

    /**
     * Test get stock with events.
     */
    @Test
    public void testGetStockWithEvents() {
        List<Event> events = Collections.singletonList(Event.HISTORY);
        Stock stock = Finv.get("AAPL", events);
        assertNotNull(stock);
        assertEquals("AAPL", stock.getTicker());
        assertNotNull(stock.getQuoteHistory());
    }

    /**
     * Test get stock with invalid ticker.
     */
    @Test
    public void testGetStockWithInvalidTicker() {
        Stock stock = Finv.get("INVALID_TICKER");
        assertNull(stock.getName());
    }

    /**
     * Test stats average close price.
     */
    @Test
    public void testStatsAverageClosePrice() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.AVERAGE_CLOSE_PRICE);
        assertEquals(0.0, result, 0.01);  // Ajuste conforme necessário
    }

    /**
     * Test stats dividend yield.
     */
    @Test
    public void testStatsDividendYield() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.DIVIDEND_YIELD);
        assertEquals(0.0, result, 0.01);  // Ajuste conforme necessário
    }

    /**
     * Test stats list.
     */
    @Test
    public void testStatsList() {
        Stock stock = new Stock("AAPL");
        List<Double> results = Finv.stats(stock, Arrays.asList(Stats.AVERAGE_CLOSE_PRICE, Stats.DIVIDEND_YIELD));
        assertEquals(2, results.size());
        // Adicione asserções conforme necessário para cada estatística
    }

    /**
     * Test export stock.
     */
    @Test
    public void testExportStock() {
        Stock stock = new Stock("AAPL");
        Finv.export(stock, ExportType.JSON);
        // Adicione asserções ou mocks conforme necessário
    }

    /**
     * Test export null stock.
     */
    @Test
    public void testExportNullStock() {
        Finv.export(null, ExportType.JSON);
        // Adicione asserções ou mocks conforme necessário
    }

}

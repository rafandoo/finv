import br.dev.rplus.finv.Finv;
import br.dev.rplus.finv.Stock;
import br.dev.rplus.finv.data.Event;
import br.dev.rplus.finv.export.ExportType;
import br.dev.rplus.finv.stats.Stats;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Finv test.
 */
class FinvTest {

    /**
     * Test get stock.
     */
    @Test
    void testGetStock() {
        Stock stock = Finv.get("AAPL");
        assertNotNull(stock);
        assertEquals("AAPL", stock.getTicker());
    }

    /**
     * Test get stock with events.
     */
    @Test
    void testGetStockWithEvents() {
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
    void testGetStockWithInvalidTicker() {
        Stock stock = Finv.get("INVALID_TICKER");
        assertNull(stock.getName());
    }

    /**
     * Test stats average close price.
     */
    @Test
    void testStatsAverageClosePrice() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.AVERAGE_CLOSE_PRICE);
        assertEquals(0.0, result, 0.01);
    }

    /**
     * Test stats dividend yield.
     */
    @Test
    void testStatsDividendYield() {
        Stock stock = new Stock("AAPL");
        double result = Finv.stats(stock, Stats.DIVIDEND_YIELD);
        assertEquals(0.0, result, 0.01);
    }

    /**
     * Test stats list.
     */
    @Test
    void testStatsList() {
        Stock stock = new Stock("AAPL");
        List<Double> results = Finv.stats(stock, Arrays.asList(Stats.AVERAGE_CLOSE_PRICE, Stats.DIVIDEND_YIELD));
        assertEquals(2, results.size());

    }

    /**
     * Test export stock.
     */
    @Test
    void testExportStock() {
        Stock stock = new Stock("AAPL");
        Finv.export(stock, ExportType.JSON);

    }

    /**
     * Test export null stock.
     */
    @Test
    void testExportNullStock() {
        Finv.export(null, ExportType.JSON);

    }
}
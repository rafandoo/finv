import finv.Finv;
import finv.Stock;
import finv.data.Event;
import finv.stats.Stats;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
//        Stock stock = YahooFinance.get("INTC");
//
//        BigDecimal price = stock.getQuote().getPrice();
//        BigDecimal change = stock.getQuote().getChangeInPercent();
//        BigDecimal peg = stock.getStats().getPeg();
//        BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
//
//        stock.print();

        List<Event> events = new ArrayList<>();
//        events.add(Event.SPLIT);
        // events.add(Event.HISTORY);
        // events.add(Event.DIVIDENDS);

        Stock intc = Finv.get("INTC", events, "2019-01-01", "2019-12-31");
        intc.print();

        List<Stats> stats = new ArrayList<>();
        stats.add(Stats.TOTAL_DIVIDENDS);
        stats.add(Stats.DIVIDEND_YIELD);

        List<Double> values = Finv.stats(intc, stats);

        for (Double value : values) {
            System.out.println(value);
        }
//        Finv.export(intc, ExportType.JSON);
//
//        Stock b3sa3 = Finv.get("B3SA3.SA", Event.DIVIDENDS, Frequency.MONTHLY);
//        b3sa3.print();


    }
}
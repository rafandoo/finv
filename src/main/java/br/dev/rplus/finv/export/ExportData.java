package br.dev.rplus.finv.export;

import br.dev.rplus.finv.Stock;
import br.dev.rplus.cup.log.LoggerCup;

public class ExportData {
    private ExportStrategy exportStrategy;


    public ExportData(ExportStrategy exportStrategy) {
        this.exportStrategy = exportStrategy;
    }

    /**
     * The function exports a stock using a specified export strategy.
     * 
     * @param stock The stock parameter is an object of the Stock class.
     */
    public void export(Stock stock) {
        exportStrategy.export(stock);
    }

    /**
     * The function exports stock data using a specified export strategy.
     *
     * @param stock      The `stock` parameter is an instance of the `Stock` class, which represents the data that needs to be exported. It likely contains information about various stocks, such as their names, prices, and quantities.
     * @param exportType The `exportType` parameter is an instance of the `ExportType` enum. It represents the type of export strategy to be used for exporting the `Stock` data.
     */
    public void export(Stock stock, ExportType exportType) {
        try {
            exportStrategy = exportType.getStrategyClass().newInstance();
            exportStrategy.export(stock);
        } catch (Exception e) {
            LoggerCup.warn("Error exporting data from stock %s.", stock.getTicker(), e);
        }
    }
}

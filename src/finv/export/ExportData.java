package finv.export;

import finv.Stock;
import finv.util.LogConfig;

import java.util.logging.Logger;

public class ExportData {
    private static final Logger logger = LogConfig.getLogger();
    private ExportStrategy exportStrategy;


    public ExportData(ExportStrategy exportStrategy) {
        this.exportStrategy = exportStrategy;
        LogConfig.configure();
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
            logger.warning("Error exporting data: " + e.getMessage());
        }
    }
}

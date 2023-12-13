package finv.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import finv.Stock;
import finv.util.LogConfig;

import java.io.FileWriter;
import java.util.logging.Logger;

public class JsonExportStrategy implements ExportStrategy {

    private static final Logger logger = LogConfig.getLogger();

    public JsonExportStrategy() {
        LogConfig.configure();
    }

    /**
     * The function exports a Stock object to a JSON file using the Jackson library in Java.
     * 
     * @param stock The "stock" parameter is an object of the Stock class.
     */
    @Override
    public void export(Stock stock) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(stock);
            String fileName = stock.getTicker() + ".json";

            try (FileWriter file = new FileWriter(fileName)) {
                file.write(json);
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + json);
            }
        } catch (Exception e) {
            logger.severe("Error exporting data: " + e.getMessage());
        }
    }
}

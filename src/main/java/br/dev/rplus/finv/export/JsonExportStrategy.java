package br.dev.rplus.finv.export;

import br.dev.rplus.cup.log.LoggerCup;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.dev.rplus.finv.Stock;

import java.io.FileWriter;

public class JsonExportStrategy implements ExportStrategy {

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
                LoggerCup.info("Successfully created JSON file.");
            }
        } catch (Exception e) {
            LoggerCup.warn("Error exporting data.", e);
        }
    }
}

package finv.export;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import finv.Stock;
import finv.util.LogConfig;

import java.io.FileWriter;
import java.util.logging.Logger;

public class XmlExportStrategy implements ExportStrategy {

    private static final Logger logger = LogConfig.getLogger();

    private XmlExportStrategy() {
        LogConfig.configure();
    }

    /**
     * The function exports a Stock object to an XML file.
     * 
     * @param stock The stock object that contains the data to be exported as XML.
     */
    @Override
    public void export(Stock stock) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            String xml = xmlMapper.writeValueAsString(stock);
            String fileName = stock.getTicker() + ".xml";

            try (FileWriter file = new FileWriter(fileName)) {
                file.write(xml);
                logger.info("Successfully created XML file...");
            }
        } catch (Exception e) {
            logger.warning("Error exporting data: " + e.getMessage());
        }
    }
}

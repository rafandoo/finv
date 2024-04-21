package br.dev.rplus.finv.export;

import br.dev.rplus.cup.log.LoggerCup;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import br.dev.rplus.finv.Stock;

import java.io.FileWriter;

public class XmlExportStrategy implements ExportStrategy {


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
                LoggerCup.info("Successfully created XML file...");
            }
        } catch (Exception e) {
            LoggerCup.warn("Error exporting data.", e);
        }
    }
}

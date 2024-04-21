package br.dev.rplus.finv.export;

public enum ExportType {
    JSON(JsonExportStrategy.class),
    XML(XmlExportStrategy.class);

    private final Class<? extends ExportStrategy> strategyClass;

    ExportType(Class<? extends ExportStrategy> strategyClass) {
        this.strategyClass = strategyClass;
    }

    /**
     * The function returns the class of the export strategy.
     * 
     * @return The method is returning a Class object that extends the ExportStrategy class.
     */
    public Class<? extends ExportStrategy> getStrategyClass() {
        return strategyClass;
    }
}
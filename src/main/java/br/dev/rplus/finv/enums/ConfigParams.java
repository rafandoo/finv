package br.dev.rplus.finv.enums;

import br.dev.rplus.cup.enums.TypedValue;

/**
 * Enum that defines the configuration parameters used by the library.
 */
public enum ConfigParams {

    /**
     * The log level of the library.
     */
    LOG_LEVEL(System.getProperty("finv.log.level", "NOTICE")),

    /**
     * The log file of the library.
     */
    LOG_FILE(Boolean.valueOf(System.getProperty("finv.log.file", "false"))),

    /**
     * The log handler of the library.
     */
    USE_LOG_HANDLER(Boolean.valueOf(System.getProperty("finv.log.handler", "false")));

    private final TypedValue value;

    ConfigParams(Object value) {
        this.value = new TypedValue(value);
    }

    /**
     * Retrieves the raw value of the configuration parameter.
     *
     * @return the raw value of the configuration parameter.
     */
    public TypedValue get() {
        return value;
    }
}

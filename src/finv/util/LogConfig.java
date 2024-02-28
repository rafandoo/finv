package finv.util;

import finv.Finv;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static boolean isConfigured = false;

    /**
     * Configures the logger.
     */
    public static synchronized void configure() {
        if (!isConfigured) {
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            try {
                FileHandler fileHandler = new FileHandler("finv.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);

                Handler[] consoleHandlers = logger.getHandlers();
                for (Handler handler : consoleHandlers) {
                    if (handler instanceof java.util.logging.ConsoleHandler) {
                        logger.removeHandler(handler);
                    }
                }

                logger.setLevel(Level.parse(Finv.LOG_LEVEL));

                isConfigured = true;
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the logger instance.
     *
     * @return the logger instance
     */
    public static Logger getLogger() {
        return logger;
    }
}

package finv.util;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static boolean isConfigured = false;

    /**
     * Configures the function.
     *
     * @param  None    There are no parameters for this function.
     * @return None    This function does not return any values.
     */
    public static synchronized void configure() {
        if (!isConfigured) {
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            try {
                FileHandler fileHandler = new FileHandler("application.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);

                Handler[] consoleHandlers = logger.getHandlers();
                for (Handler handler : consoleHandlers) {
                    if (handler instanceof java.util.logging.ConsoleHandler) {
                        logger.removeHandler(handler);
                    }
                }

                logger.setLevel(Level.INFO);

                isConfigured = true;
            } catch (IOException e) {
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

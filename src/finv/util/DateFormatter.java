package finv.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

public class DateFormatter {

    private static final Logger logger = LogConfig.getLogger();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private DateFormatter() {
        LogConfig.configure();
    }

    /**
     * Formats a given date using the DATE_FORMATTER and returns the formatted date as a string.
     *
     * @param date the date to be formatted
     * @return the formatted date as a string
     */
    public static String format(Date date) {
        try {
            return DATE_FORMATTER.format(date);
        } catch (Exception e) {
            logger.warning("Failed to format date: " + date + ", error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Formats a given date using the DATE_FORMATTER and returns the formatted date as a string.
     *
     * @param date the LocalDate to be formatted
     * @return the formatted date as a string
     */
    public static String format(LocalDate date) {
        try {
            return DATE_FORMATTER.format(date);
        } catch (Exception e) {
            logger.warning("Failed to format date: " + date + ", error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parses a given date string into a Date object.
     *
     * @param date the date string to be parsed
     * @return the parsed Date object
     */
    public static Date parse(String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            logger.warning("Failed to parse date: " + date + ", error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Generates a formatted timestamp from a given date.
     *
     * @param date the date to format
     * @return the formatted timestamp
     */
    public static String timestampFormat(Date date) {
        try {
            return String.valueOf(new Timestamp(date.getTime()).getTime() / 1000);
        } catch (Exception e) {
            logger.warning("Failed to format timestamp: " + date + ", error: " + e.getMessage());
        }
        return null;
    }
}

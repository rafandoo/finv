package finv.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Formats a given date using the DATE_FORMATTER and returns the formatted date as a string.
     *
     * @param  date  the date to be formatted
     * @return       the formatted date as a string
     */
    public static String format(Date date) {
        return DATE_FORMATTER.format(date);
    }

    /**
     * Parses a given date string into a Date object.
     *
     * @param  date  the date string to be parsed
     * @return       the parsed Date object
     */
    public static Date parse(String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter a data", e);
        }
    }

    /**
     * Generates a formatted timestamp from a given date.
     *
     * @param  date  the date to format
     * @return       the formatted timestamp
     */
    public static String timestampFormat(Date date) {
        return String.valueOf(new Timestamp(date.getTime()).getTime() / 1000);
    }


}

package ngsdiaglim.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtils {

    public static String formatLocalDate(LocalDate localDate) {
        return formatLocalDate(localDate, "dd/MM/yyyy");
    }

    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }

}

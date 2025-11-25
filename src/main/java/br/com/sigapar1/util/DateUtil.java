package br.com.sigapar1.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        return date == null ? "" : DF.format(date);
    }

    public static String format(LocalDateTime date) {
        return date == null ? "" : DTF.format(date);
    }

    public static LocalDate parseDate(String x) {
        if (x == null || x.isBlank()) return null;
        return LocalDate.parse(x, ISO);
    }
}

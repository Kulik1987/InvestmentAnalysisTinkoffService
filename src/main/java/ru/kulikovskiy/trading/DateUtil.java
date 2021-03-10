package ru.kulikovskiy.trading;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDateTime getLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString.substring(0,19), formatter);
        return dateTime;
    }

    public static String getStringFromLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return localDateTime.format(formatter) + ".000Z";
    }
}

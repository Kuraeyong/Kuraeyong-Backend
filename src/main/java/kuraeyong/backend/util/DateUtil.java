package kuraeyong.backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String getCurrTime(int hour, int min) {
        return makeDoubleDigits(hour) + makeDoubleDigits(min) + "00";
    }

    public static String plusMinutes(String currTime, int minutes) {
        int hour = Integer.parseInt(currTime.substring(0, 2));
        int min = Integer.parseInt(currTime.substring(2, 4));
        int sec = Integer.parseInt(currTime.substring(4, 6));

        LocalDateTime localDateTime = LocalDateTime.of(2000, 4, 13, hour, min, sec);
        LocalDateTime result = localDateTime.plusMinutes(minutes);

        return result.format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    private static String makeDoubleDigits(int num) {
        if (num / 100 != 0) {   // 세 자릿수 이상
            return null;
        }
        if (num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }
}

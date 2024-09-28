package kuraeyong.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public final static int DATE_CHANGE_TIME = 30000;  // 날짜 변경 기준 시간
    public final static int CORRECTION_VALUE = 240000; // 날짜 변경 기준 시간에 따른 보정 값

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

    public static int getTimeForCompare(String arvTm, String dptTm) {
        int time = isValidTime(dptTm) ? Integer.parseInt(arvTm) : Integer.parseInt(dptTm);

        return (time < DATE_CHANGE_TIME) ? time + CORRECTION_VALUE : time;
    }

    public static int getTimeForCompare(String time) {
        return getTimeForCompare(time, "null");   // 순서 상관 X
    }

    public static int timeToMinute(int time) {
        int hour = time / 10000;
        int min = (time % 10000) / 100;

        return hour * 60 + min;
    }

    /**
     * 입력 순서에 상관없이, 두 시간의 차를 반환
     */
    public static int getMinDiff(String time1, String time2) {
        if (time1.compareTo(time2) > 0) {
            String temp = time1;
            time1 = time2;
            time2 = temp;
        }
        int timeVal1 = timeToMinute(getTimeForCompare(time1));
        int timeVal2 = timeToMinute(getTimeForCompare(time2));

        return timeVal2 - timeVal1;
    }

    public static boolean isWithinNMinutes(String earlierTime, String laterTime, int n) {
        int time1 = getTimeForCompare(plusMinutes(earlierTime, n));
        int time2 = getTimeForCompare(laterTime);

        return time1 >= time2;
    }

    public static boolean isValidTime(String str) {
        return !str.matches("[0-9]{6}");
    }
}

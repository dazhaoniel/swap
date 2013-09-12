package org.solazo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daniel Zhao on 8/17/13.
 */
public class DateUtils {
    private static DateFormat dateFormat;
    private static final String datePatternPHP = "yyyy-MM-dd HH:mm:ss";
    private static Date date;

    public static String getDate() {
        dateFormat = new SimpleDateFormat(datePatternPHP);
        date = new Date();

        return dateFormat.format(date).toString();
    }

    public static boolean isDay() {
        date = new Date();
        if (date.getHours() <= 19 && date.getHours() >= 6) {
            return true;
        }
        return false;
    }

}

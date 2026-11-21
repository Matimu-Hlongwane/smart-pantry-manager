package com.example.smartpantrymanagerjava.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for date parsing, formatting, and expiry checking.
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.US);
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        return getSimpleDateFormat().format(date);
    }

    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return getSimpleDateFormat().parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true; // Expiry date is optional
        }
        return parseDate(dateStr) != null;
    }

    public static boolean isExpired(String expiryDateStr) {
        Date expiryDate = parseDate(expiryDateStr);
        if (expiryDate == null) return false;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return expiryDate.before(today.getTime());
    }

    public static boolean isExpiringSoon(String expiryDateStr, int daysThreshold) {
        Date expiryDate = parseDate(expiryDateStr);
        if (expiryDate == null) return false;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar thresholdCalendar = Calendar.getInstance();
        thresholdCalendar.set(Calendar.HOUR_OF_DAY, 23);
        thresholdCalendar.set(Calendar.MINUTE, 59);
        thresholdCalendar.set(Calendar.SECOND, 59);
        thresholdCalendar.add(Calendar.DAY_OF_YEAR, daysThreshold);

        return !expiryDate.before(today.getTime()) && expiryDate.before(thresholdCalendar.getTime());
    }
}

package com.ramitsuri.expensemanagerrewrite.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateHelper {
    private static final String FORMAT_FRIENDLY = "MMM dd"; // Sep 21
    private static final String FORMAT_EXPANDED = "EEEE, MMMM"; // Thursday, September 21
    private static final String FORMAT_DAY = "dd"; // 21
    private static String[] DAY_SUFFIXES =
            {"0th", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
                    "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
                    "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
                    "30th", "31st"};
    private static final long MILLI_SECONDS_IN_DAY = 86400000;
    private static final long SHEETS_DATE_OFFSET = 25569;

    public static String getFriendlyDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_FRIENDLY, Locale.getDefault());
        return format.format(date);
    }

    public static String getExpandedDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_EXPANDED, Locale.getDefault());

        SimpleDateFormat formatDayOfMonth = new SimpleDateFormat(FORMAT_DAY, Locale.getDefault());
        int day = Integer.parseInt(formatDayOfMonth.format(date));

        return format.format(date) + " " + DAY_SUFFIXES[day];
    }

    public static double toSheetsDate(long date) {
        return (double)date / MILLI_SECONDS_IN_DAY + SHEETS_DATE_OFFSET;
    }

    public static long fromSheetsDate(long sheetsDate) {
        return (sheetsDate - SHEETS_DATE_OFFSET) * MILLI_SECONDS_IN_DAY;
    }

    public static long toDaysSinceStartOfTime(long date) {
        return date / MILLI_SECONDS_IN_DAY;
    }

    public static long toDay(long daysFromStartOfTime) {
        return daysFromStartOfTime * MILLI_SECONDS_IN_DAY;
    }
}

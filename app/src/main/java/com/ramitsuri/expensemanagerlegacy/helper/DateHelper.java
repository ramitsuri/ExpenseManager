package com.ramitsuri.expensemanagerlegacy.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateHelper {

    private static int FIRST_DAY_OF_WEEK = Calendar.SUNDAY;

    public static String getPrettyDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(calendar.getTime());
    }

    public static double getDateForSheet(long date) {
        TimeZone timeZone = TimeZone.getDefault();

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Start date time
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.YEAR, 1899);
        long timeStart = calendar.getTimeInMillis();

        // End date time
        calendar.set(Calendar.DAY_OF_MONTH, getDayFromLongDate(date));
        calendar.set(Calendar.MONTH, getMonthFromLongDate(date) - 1);
        calendar.set(Calendar.YEAR, getYearFromLongDate(date));
        long timeEnd = calendar.getTimeInMillis();

        long offset = 0;
        if (timeZone.observesDaylightTime()) {
            offset = timeZone.getOffset(calendar.getTimeInMillis());
        }

        return TimeUnit.MILLISECONDS.toDays(timeEnd - timeStart - offset);
    }

    public static String getPrettyDate(int year1, int month1, int day1,
            int year2, int month2, int day2) {
        String date1 = getPrettyDate(year1, month1, day1);
        String date2 = getPrettyDate(year2, month2, day2);
        if (year1 == year2) {
            date1 = date1.split(",")[0].trim();
        }
        String date = date1 + " - " + date2;
        return date;
    }

    public static String getPrettyMonthDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");
        return df.format(calendar.getTime());
    }

    public static String getPrettyDate(long date1, long date2) {
        int year1 = getYearFromLongDate(date1);
        int year2 = getYearFromLongDate(date2);
        int month1 = getMonthFromLongDate(date1) - 1;
        int month2 = getMonthFromLongDate(date2) - 1;
        int day1 = getDayFromLongDate(date1);
        int day2 = getDayFromLongDate(date2);

        return getPrettyDate(year1, month1, day1, year2, month2, day2);
    }

    public static String getJustTheDayOfMonth(long date) {
        return String.valueOf(date % 100);
    }

    public static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(calendar.getTime());
    }

    public static long getTodaysLongDate() {
        Calendar calendar = Calendar.getInstance();
        return getLongDateForDB(calendar.getTime());
    }

    public static long getLongDateForDB(int year, int month, int day) {
        long date;
        month = month + 1;
        date = year * 100 + month;
        date = date * 100 + day;
        return date;
    }

    public static long getLongDateForDB(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getLongDateForDB(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int difference = getDayDifference(FIRST_DAY_OF_WEEK, today);
        calendar.add(Calendar.DATE, difference);
        return calendar.getTime();
    }

    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int difference = getDayDifference(FIRST_DAY_OF_WEEK, today);
        calendar.add(Calendar.DATE, difference);
        calendar.add(Calendar.DATE, 6);
        return calendar.getTime();
    }

    private static int getDayDifference(int firstDayOfWeek, int today) {
        if (firstDayOfWeek == Calendar.MONDAY && today == Calendar.SUNDAY) {
            return -6;
        }
        return firstDayOfWeek - today;
    }

    public static int getDayFromLongDate(long date) {
        return (int)(date % 100);
    }

    public static int getMonthFromLongDate(long date) {
        return (int)((date / 100) % 100);
    }

    public static int getYearFromLongDate(long date) {
        return (int)((date / 100) / 100);
    }

    public static String getDateTimeFromTimeInMillis(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }
}

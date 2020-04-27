package com.ramitsuri.expensemanager.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

public class DateHelper {
    private static final String FORMAT_FRIENDLY = "MMM dd"; // Sep 21
    private static final String FORMAT_EXPANDED = "EEEE, MMMM"; // Thursday, September 21
    private static final String FORMAT_MONTH = "MMMM"; // September
    private static final String FORMAT_DAY = "dd"; // 21
    private static final String FORMAT_FULL = "EE MMM dd HH:mm:ss zzz yyyy";
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

    public static String getFullDate(long date, TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_FULL, Locale.getDefault());
        format.setTimeZone(timeZone);
        return format.format(date);
    }

    public static String getMonth(long date) {
        return getMonth(date, null);
    }

    public static String getMonth(long date, TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = AppHelper.getTimeZone();
        }
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_MONTH, Locale.getDefault());
        format.setTimeZone(timeZone);
        return format.format(date);
    }

    public static String getJustDay(long date) {
        SimpleDateFormat formatDayOfMonth = new SimpleDateFormat(FORMAT_DAY, Locale.getDefault());
        return formatDayOfMonth.format(date);
    }

    public static long toSheetsDate(long date) {
        return toSheetsDate(date, null);
    }

    public static long toSheetsDate(long date, @Nullable TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = AppHelper.getTimeZone();
        }
        long offset = 0;
        if (timeZone.observesDaylightTime()) {
            offset = timeZone.getOffset(date);
        }
        return (date + offset) / MILLI_SECONDS_IN_DAY + SHEETS_DATE_OFFSET;
    }

    public static long fromSheetsDate(long sheetsDate) {
        return fromSheetsDate(sheetsDate, null);
    }

    public static long fromSheetsDate(long sheetsDate, @Nullable TimeZone timeZone) {
        long potentialDate = (sheetsDate - SHEETS_DATE_OFFSET) * MILLI_SECONDS_IN_DAY;
        if (timeZone == null) {
            timeZone = AppHelper.getTimeZone();
        }
        long offset = 0;
        if (timeZone.observesDaylightTime()) {
            offset = timeZone.getOffset(potentialDate);
        }
        return potentialDate - offset;
    }

    public static int getYearFromDate(LocalDate localDate) {
        return localDate.getYear();
    }

    /**
     * return month in range 0-11
     */
    public static int getMonthFromDate(LocalDate localDate) {
        return localDate.getMonthValue() - 1;
    }

    public static int getDayFromDate(LocalDate localDate) {
        return localDate.getDayOfMonth();
    }

    public static LocalDate getLocalDate(Date date) {
        return getLocalDate(date, null);
    }

    public static LocalDate getLocalDate(Date date, TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = AppHelper.getTimeZone();
        }
        ZoneId zoneId = timeZone.toZoneId();
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    public static int getMonthIndexFromDate(long date) {
        return getMonthIndexFromDate(date, null);
    }

    public static int getMonthIndexFromDate(long date, TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = AppHelper.getTimeZone();
        }
        return getMonthFromDate(getLocalDate(new Date(date), timeZone));
    }

    /**
     * Takes in month in range 0-11
     */
    public static long getDateFromYearMonthDay(int year, int month, int day) {
        LocalDate localDate = LocalDate.of(year, month + 1, day);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Returns a Duration object for difference between current time and desired hour of the day.
     * <p>
     * If current time is past the desired hour of the day, then difference between current time
     * and next day's desired hour of the day is returned.
     */
    public static Duration getDelayForPeriodicWork(Calendar calendar, int desiredHourOfDay) {
        long timeNow = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, desiredHourOfDay);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long timeDesired = calendar.getTimeInMillis();
        if (timeNow > timeDesired) {
            timeDesired = timeDesired + TimeUnit.DAYS.toMillis(1);
        }
        return Duration.ofMillis(timeDesired - timeNow);
    }

    public static long getFirstDayOfCurrentYear() {
        Calendar calendar = Calendar.getInstance(AppHelper.getTimeZone());
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getLastDayOfCurrentYear() {
        Calendar calendar = Calendar.getInstance(AppHelper.getTimeZone());
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}

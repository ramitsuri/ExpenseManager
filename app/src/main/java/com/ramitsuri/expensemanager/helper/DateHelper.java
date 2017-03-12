package com.ramitsuri.expensemanager.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {

    private static int FIRST_DAY_OF_WEEK = Calendar.MONDAY;

    public static String getPrettyDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(calendar.getTime());
    }

    public static String getJustTheDayOfMonth(long date){
        return String.valueOf(date % 100);
    }

    public static String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(calendar.getTime());
    }

    public static long getLongDateForDB(int year, int month, int day){
        long date;
        month = month + 1;
        date = year * 100 + month;
        date = date * 100 + day;
        return date;
    }

    public static long getLongFirstDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int difference = FIRST_DAY_OF_WEEK - today;
        calendar.add(Calendar.DATE, difference);
        return getLongDateForDB(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static long getDayFromLongDate(long date){
        return date % 100;
    }

    private static long getMonthFromLongDate(long date){
        return (date / 100) % 100;
    }

    private static long getYearFromLongDate(long date){
        return (date / 100) / 100;
    }
}

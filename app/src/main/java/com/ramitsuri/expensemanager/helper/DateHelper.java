package com.ramitsuri.expensemanager.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {

    public static String getPrettyDate(long date){

        return "";
    }

    public static String getJustTheDayOfMonth(long date){
        return String.valueOf(date % 100);
    }

    public static String getTodaysDate(){
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(rightNow.getTime());
    }

    public static long getLongDate(int year, int month, int day){
        long date;
        month = month + 1;
        date = year * 100 + month;
        date = date * 100 + day;
        return date;
    }

}

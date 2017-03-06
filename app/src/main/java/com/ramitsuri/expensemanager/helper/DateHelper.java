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

}

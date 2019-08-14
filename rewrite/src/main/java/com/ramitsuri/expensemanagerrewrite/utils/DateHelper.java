package com.ramitsuri.expensemanagerrewrite.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateHelper {
    private static final String FORMAT_FRIENDLY = "MMM dd";

    public static String getFriendlyDate(long date){
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_FRIENDLY, Locale.getDefault());
        return format.format(date);
    }
}

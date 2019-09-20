package com.ramitsuri.expensemanagerlegacy.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

public class DateHelperTest {

    @Test
    public void getDateForSheet() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        int start = 43466;

        for (int i = 1; i <= 3650; i++) {
            long longDate = DateHelper.getLongDateForDB(calendar1.getTime());
            double doubleDate = DateHelper.getDateForSheet(longDate);
            System.out.println(calendar1.getTime() + " " + isEqual(doubleDate, start) + " ");
            Assert.assertEquals(start, (int)doubleDate);
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
            start = start + 1;
        }
    }

    private static String isEqual(double doubleDate, int start) {
        if ((int)doubleDate == start) {
            return "TRUE";
        } else {
            return "FALS";
        }
    }
}

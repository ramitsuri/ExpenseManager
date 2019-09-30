package com.ramitsuri.expensemanagerrewrite.utils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DateHelperTest {
    @Test
    public void testFriendlyDate() {
        assertEquals("Aug 14", DateHelper.getFriendlyDate(1565818852014L));
        assertEquals("Aug 14", DateHelper.getFriendlyDate(1565829852014L));
    }

    @Test
    public void testExpandedDate() {
        long base = 1565818852014L;
        long oneDay = 86400000;
        assertEquals("Sunday, July 28th", DateHelper.getExpandedDate(base - 17 * oneDay));
        assertEquals("Monday, July 29th", DateHelper.getExpandedDate(base - 16 * oneDay));
        assertEquals("Tuesday, July 30th", DateHelper.getExpandedDate(base - 15 * oneDay));
        assertEquals("Wednesday, July 31st", DateHelper.getExpandedDate(base - 14 * oneDay));
        assertEquals("Thursday, August 1st", DateHelper.getExpandedDate(base - 13 * oneDay));
        assertEquals("Friday, August 2nd", DateHelper.getExpandedDate(base - 12 * oneDay));
        assertEquals("Saturday, August 3rd", DateHelper.getExpandedDate(base - 11 * oneDay));
        assertEquals("Sunday, August 4th", DateHelper.getExpandedDate(base - 10 * oneDay));
        assertEquals("Monday, August 5th", DateHelper.getExpandedDate(base - 9 * oneDay));
        assertEquals("Tuesday, August 6th", DateHelper.getExpandedDate(base - 8 * oneDay));
        assertEquals("Wednesday, August 7th", DateHelper.getExpandedDate(base - 7 * oneDay));
        assertEquals("Thursday, August 8th", DateHelper.getExpandedDate(base - 6 * oneDay));
        assertEquals("Friday, August 9th", DateHelper.getExpandedDate(base - 5 * oneDay));
        assertEquals("Saturday, August 10th", DateHelper.getExpandedDate(base - 4 * oneDay));
        assertEquals("Sunday, August 11th", DateHelper.getExpandedDate(base - 3 * oneDay));
        assertEquals("Monday, August 12th", DateHelper.getExpandedDate(base - 2 * oneDay));
        assertEquals("Tuesday, August 13th", DateHelper.getExpandedDate(base - oneDay));
        assertEquals("Wednesday, August 14th", DateHelper.getExpandedDate(base));
        assertEquals("Thursday, August 15th", DateHelper.getExpandedDate(base + oneDay));
        assertEquals("Friday, August 16th", DateHelper.getExpandedDate(base + 2 * oneDay));
        assertEquals("Saturday, August 17th", DateHelper.getExpandedDate(base + 3 * oneDay));
        assertEquals("Sunday, August 18th", DateHelper.getExpandedDate(base + 4 * oneDay));
        assertEquals("Monday, August 19th", DateHelper.getExpandedDate(base + 5 * oneDay));
        assertEquals("Tuesday, August 20th", DateHelper.getExpandedDate(base + 6 * oneDay));
        assertEquals("Wednesday, August 21st", DateHelper.getExpandedDate(base + 7 * oneDay));
        assertEquals("Thursday, August 22nd", DateHelper.getExpandedDate(base + 8 * oneDay));
        assertEquals("Friday, August 23rd", DateHelper.getExpandedDate(base + 9 * oneDay));
    }

    @Test
    public void testToSheetsDate() {
        int startSheetDate = 43466;
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            long longDate = DateHelper.toSheetsDate(calendar1.getTime().getTime());
            assertEquals(startSheetDate, (int)longDate);
            System.out.println(calendar1.getTime() + " " + longDate);
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
            startSheetDate = startSheetDate + 1;
        }
    }

    @Test
    public void testFromSheetsDate() {
        int startSheetDate = 43466;
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            assertTrue(calendar1.getTime().getTime() - DateHelper.fromSheetsDate(startSheetDate) <=
                    86400000);
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
            startSheetDate = startSheetDate + 1;
        }
    }

    @Test
    public void testGetYear() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            LocalDate localDate = DateHelper.getLocalDate(calendar1.getTime());
            assertEquals(calendar1.get(Calendar.YEAR), DateHelper.getYearFromDate(localDate));
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Test
    public void testGetMonth() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            LocalDate localDate = DateHelper.getLocalDate(calendar1.getTime());
            assertEquals(calendar1.get(Calendar.MONTH) + 1, DateHelper.getMonthFromDate(localDate));
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Test
    public void testGetDay() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            LocalDate localDate = DateHelper.getLocalDate(calendar1.getTime());
            assertEquals(calendar1.get(Calendar.DAY_OF_MONTH),
                    DateHelper.getDayFromDate(localDate));
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Test
    public void testGetDateFromYearMonthDay() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            int year = calendar1.get(Calendar.YEAR);
            int month = calendar1.get(Calendar.MONTH);
            int day = calendar1.get(Calendar.DAY_OF_MONTH);
            long timeInMillis = DateHelper.getDateFromYearMonthDay(year, month + 1, day);
            Date actualDate = new Date(timeInMillis);
            assertEquals(calendar1.getTime(), actualDate);
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Test
    public void testJustDay() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2019);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 3650; i++) {
            int day = calendar1.get(Calendar.DAY_OF_MONTH);
            assertEquals(day,
                    Integer.parseInt(DateHelper.getJustDay(calendar1.getTime().getTime())));
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}

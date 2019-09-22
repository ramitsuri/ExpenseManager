package com.ramitsuri.expensemanagerrewrite.utils;

import org.junit.Test;

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
}

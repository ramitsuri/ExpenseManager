package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FilterTest {
    private final TimeZone timeZone = TimeZone.getDefault();

    @Test
    public void testQueryJustIncome() {
        Filter filter = new Filter(timeZone);
        assertEquals("SELECT * FROM expense", filter.toQuery().getSql());

        filter.setIncome(true);
        assertEquals("SELECT * FROM expense WHERE is_income = ?",
                filter.toQuery().getSql());
        assertEquals(1, filter.toQuery().getArgCount());

        filter.setIncome(false);
        assertEquals("SELECT * FROM expense WHERE is_income = ?",
                filter.toQuery().getSql());
        assertEquals(1, filter.toQuery().getArgCount());
    }

    @Test
    public void testJustDateTime() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        // Add month 3
        filter.addMonth(3);
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());

        // Add month 3 again to make sure query didn't change
        filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());

        // Add month 3 and 4
        filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addYear(2020);
        filter.addMonth(4);
        filter.addMonth(3);
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        // Add month 3 and 4 again to make sure query didn't change
        filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        filter.addMonth(4);
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        filter.removeMonth(3);
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());
    }

    @Test
    public void testDateTimeAndIncome() {
        // Month 3, income true
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        filter.setIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(3, filter.toQuery().getArgCount());

        // Month 3, income true again
        filter.addMonth(3);
        filter.setIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(3, filter.toQuery().getArgCount());

        // Month 3 and 4, income true
        filter.addMonth(4);
        filter.setIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(5, filter.toQuery().getArgCount());
    }

    @Test
    public void testJustCategory() {
        // Month 3, categories
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        filter.addCategory("Food")
                .addCategory("Travel");
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) ) AND category IN (?,?)",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        // Remove empty category, no change
        filter.removeCategory("");
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) ) AND category IN (?,?)",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        // Remove not present category, no change
        filter.removeCategory("Gas");
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) ) AND category IN (?,?)",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        // Remove real category, query should change
        filter.removeCategory("Food");
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) ) AND category IN (?)",
                filter.toQuery().getSql());
        assertEquals(3, filter.toQuery().getArgCount());
    }

    @Test
    public void testJustRecordType() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3)
                .addMonth(4)
                .setRecordType(RecordType.MONTHLY);
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) ) AND record_type = ?",
                filter.toQuery().getSql());
        assertEquals(5, filter.toQuery().getArgCount());
    }

    @Test
    public void testAllFilters() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        filter.addMonth(4);
        filter.setIncome(true);
        filter.setSynced(true);
        filter.setStarred(true);
        filter.addCategory("Food");
        filter.addCategory("Travel");
        filter.addPaymentMethod("Citi");
        filter.addPaymentMethod("Credit");
        filter.addPaymentMethod("Cash");
        filter.setRecordType(RecordType.ANNUAL);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) ) AND category IN (?,?) AND payment_method IN (?,?,?) AND is_synced = ? AND is_starred = ? AND record_type = ?",
                filter.toQuery().getSql());
        assertEquals(13, filter.toQuery().getArgCount());
    }

    @Test
    public void parcelableTest() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3);
        filter.addMonth(4);
        filter.setIncome(true);
        filter.setSynced(true);
        filter.setStarred(true);
        filter.addCategory("Food");
        filter.addCategory("Travel");
        filter.addPaymentMethod("Citi");
        filter.addPaymentMethod("Credit");
        filter.addPaymentMethod("Cash");
        filter.setRecordType(RecordType.MONTHLY);

        Parcel parcel = Parcel.obtain();
        filter.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Filter fromParcel = Filter.CREATOR.createFromParcel(parcel);
        assertEquals(filter.toQuery().getSql(), fromParcel.toQuery().getSql());
        assertEquals(filter.toQuery().getArgCount(),
                fromParcel.toQuery().getArgCount());
    }

    @Test
    public void testToUpdateSyncedQuery() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(3)
                .addMonth(5);

        assertEquals(
                "UPDATE expense SET is_synced = 0 WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toUpdateSyncedQuery().getSql());
        assertEquals(4, filter.toUpdateSyncedQuery().getArgCount());
    }

    @Test
    public void testAddRemovePeriods() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(1);
        filter.removeMonth(1);

        assertEquals("SELECT * FROM expense", filter.toQuery().getSql());
        assertEquals(0, filter.toQuery().getArgCount());
    }

    @Test
    public void testMonthYear() {
        Filter filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(2);
        assertEquals(1, filter.getMonths().size());
        assertTrue(filter.getMonths().contains(2));

        filter.removeMonth(2);
        assertTrue(filter.getMonths().isEmpty());

        filter.addYear(2020);
        assertEquals(1, filter.getYears().size());

        filter.removeYear(2020);
        assertTrue(filter.getYears().isEmpty());
    }
}

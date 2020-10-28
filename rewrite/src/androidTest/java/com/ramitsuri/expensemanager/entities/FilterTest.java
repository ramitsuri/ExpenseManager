package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.utils.SqlBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.TimeZone;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FilterTest {

    @Test
    public void testQueryJustIncome() {
        Filter filter = new Filter();
        assertEquals("SELECT * FROM expense", filter.toQuery().getSql());

        filter.setIsIncome(true);
        assertEquals("SELECT * FROM expense WHERE is_income = ?", filter.toQuery().getSql());
        assertEquals(1, filter.toQuery().getArgCount());

        filter.setIsIncome(false);
        assertEquals("SELECT * FROM expense WHERE is_income = ?", filter.toQuery().getSql());
        assertEquals(1, filter.toQuery().getArgCount());
    }

    @Test
    public void testJustDateTime() {
        Filter filter = new Filter();
        // Add month 3
        filter.addMonthIndex(3, TimeZone.getDefault());
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());

        // Add month 3 again to make sure query didn't change
        filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault());
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());

        // Add month 3 and 4
        filter = new Filter();
        filter.addMonthIndex(4, TimeZone.getDefault());
        filter.addMonthIndex(3, TimeZone.getDefault());
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        // Add month 3 and 4 again to make sure query didn't change
        filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault());
        filter.addMonthIndex(4, TimeZone.getDefault());
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(4, filter.toQuery().getArgCount());

        filter.removeMonthIndex(3);
        assertEquals("SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(2, filter.toQuery().getArgCount());
    }

    @Test
    public void testDateTimeAndIncome() {
        // Month 3, income true
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault());
        filter.setIsIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(3, filter.toQuery().getArgCount());

        // Month 3, income true again
        filter.addMonthIndex(3, TimeZone.getDefault());
        filter.setIsIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(3, filter.toQuery().getArgCount());

        // Month 3 and 4, income true
        filter.addMonthIndex(4, TimeZone.getDefault());
        filter.setIsIncome(true);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toQuery().getSql());
        assertEquals(5, filter.toQuery().getArgCount());
    }

    @Test
    public void testJustCategory() {
        // Month 3, categories
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault());
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
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault())
                .addMonthIndex(4, TimeZone.getDefault())
                .setRecordType(RecordType.MONTHLY);
        assertEquals(
                "SELECT * FROM expense WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) ) AND record_type = ?",
                filter.toQuery().getSql());
        assertEquals(5, filter.toQuery().getArgCount());
    }

    @Test
    public void testAllFilters() {
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault())
                .addMonthIndex(4, TimeZone.getDefault())
                .setIsIncome(true)
                .setSynced(true)
                .setIsStarred(true)
                .addCategory("Food")
                .addCategory("Travel")
                .addPaymentMethod("Citi")
                .addPaymentMethod("Credit")
                .addPaymentMethod("Cash")
                .setRecordType(RecordType.ANNUAL);
        assertEquals(
                "SELECT * FROM expense WHERE is_income = ? AND ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) ) AND category IN (?,?) AND payment_method IN (?,?,?) AND is_synced = ? AND is_starred = ? AND record_type = ?",
                filter.toQuery().toString());
        assertEquals(13, filter.toQuery().getArgCount());
    }

    @Test
    public void parcelableTest() {
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault())
                .addMonthIndex(4, TimeZone.getDefault())
                .setIsIncome(true)
                .setSynced(true)
                .setIsStarred(true)
                .addCategory("Food")
                .addCategory("Travel")
                .addPaymentMethod("Citi")
                .addPaymentMethod("Credit")
                .addPaymentMethod("Cash")
                .setRecordType(RecordType.MONTHLY);

        Parcel parcel = Parcel.obtain();
        filter.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Filter fromParcel = Filter.CREATOR.createFromParcel(parcel);
        assertEquals(filter.toQuery().getSql(), fromParcel.toQuery().getSql());
        assertEquals(filter.toQuery().getArgCount(), fromParcel.toQuery().getArgCount());
    }

    @Test
    public void testToUpdateSyncedQuery() {
        Filter filter = new Filter();
        filter.addMonthIndex(3, TimeZone.getDefault())
                .addMonthIndex(5, TimeZone.getDefault());

        assertEquals("UPDATE expense SET is_synced = 0 WHERE ( (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?) )",
                filter.toUpdateSyncedQuery().getSql());
        assertEquals(4, filter.toUpdateSyncedQuery().getArgCount());
    }
}

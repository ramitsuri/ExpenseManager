package com.ramitsuri.expensemanager;

import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.dao.ExpenseDao;
import com.ramitsuri.expensemanager.data.dummy.Expenses;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExpenseDatabaseTest extends BaseDatabaseTest {

    private ExpenseDao mExpenseDao;
    private final TimeZone timeZone = TimeZone.getDefault();

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mExpenseDao = mDb.expenseDao();

        for (Expense expense : Expenses.all()) {
            mExpenseDao.insert(expense);
        }
    }

    @Test
    public void testGetAll() throws Exception {
        // Get all
        Assert.assertEquals(Expenses.getExpenses().size(), mExpenseDao.getExpenses().size());
        Log.d(TAG, mExpenseDao.getExpenses().toString());
    }

    @Test
    public void testGetAllStarred() throws Exception {
        // Get all starred
        Assert.assertEquals(
                Expenses.getAllStarred().size(),
                mExpenseDao.getAllStarred().size());
    }

    /**
     * This test fails because is no longer being updated. Will be removed at some point
     */
    @Test
    public void testGetExpenseWithIdentifier() {
        Expense expense = mExpenseDao.getExpense("79483hfijdsnfosjd");
        Assert.assertNull(expense);
    }

    @Test
    public void testGetAllForDateRange() throws Exception {
        // get All within date range
        long start = Expenses.BASE_DATE_TIME;
        long end = Expenses.BASE_DATE_TIME + Expenses.ONE_DAY;
        Assert.assertEquals(Expenses.getAllForDateRange(start, end).size(),
                mExpenseDao.getExpensesForDateRange(start, end).size());
        start = Expenses.BASE_DATE_TIME - Expenses.ONE_DAY;
        end = Expenses.BASE_DATE_TIME + 2 * Expenses.ONE_DAY;
        Assert.assertEquals(Expenses.getAllForDateRange(start, end).size(),
                mExpenseDao.getExpensesForDateRange(start, end).size());
        start = Expenses.BASE_DATE_TIME - 3 * Expenses.ONE_DAY;
        end = Expenses.BASE_DATE_TIME + 3 * Expenses.ONE_DAY;
        Assert.assertEquals(Expenses.getAllForDateRange(start, end).size(),
                mExpenseDao.getExpensesForDateRange(start, end).size());
    }

    @Test
    public void testDeleteAll() throws Exception {
        // delete all
        mExpenseDao.deleteAll();
        Assert.assertEquals(
                0,
                mExpenseDao.getExpenses().size());
    }

    @Test
    public void testAddAll() throws Exception {
        // add all
        mExpenseDao.deleteAll();
        mExpenseDao.insert(Expenses.all());

        Assert.assertEquals(
                Expenses.all().size(),
                mExpenseDao.getExpenses().size());
    }

    @Test
    public void testSetStarred() throws Exception {
        // set starred
        List<Expense> expenses = mExpenseDao.getExpenses();
        for (Expense expense : expenses) {
            if (!expense.isStarred()) {
                mExpenseDao.setStarred(expense.getId());
                break;
            }
        }
        Assert.assertEquals(
                Expenses.getAllStarred().size() + 1,
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void testSetUnstarred() throws Exception {
        // set unstarred
        mExpenseDao.setUnstarred(mExpenseDao.getExpenses().get(3).getId());
        Assert.assertEquals(
                Expenses.getAllStarred().size() - 1,
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void testGetForFilter() {
        Filter filter;
        for (int i = 1; i <= 12; i++) {
            filter = new Filter(timeZone);
            filter.addYear(2020);
            filter.addMonth(i);
            Assert.assertEquals(Expenses.getForFilter(filter).size(),
                    mExpenseDao.getForFilter(filter).size());
        }
        filter = new Filter(timeZone);
        for (int i = 1; i <= 12; i++) {
            filter.addYear(2020);
            filter.addMonth(i);
            Assert.assertEquals(Expenses.getForFilter(filter).size(),
                    mExpenseDao.getForFilter(filter).size());
        }
        filter = new Filter(timeZone);
        for (int i = 1; i <= 12; i++) {
            filter.addYear(2020);
            filter.addMonth(i);
            Assert.assertEquals(Expenses.getForFilter(filter).size(),
                    mExpenseDao.getForFilter(filter).size());
        }

        filter = new Filter(timeZone);
        Assert.assertEquals(Expenses.getForFilter(filter).size(),
                mExpenseDao.getForFilter(filter).size());

        filter = new Filter(timeZone);
        Assert.assertEquals(Expenses.getForFilter(filter).size(),
                mExpenseDao.getForFilter(filter).size());

        filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(7);
        filter.addMonth(4);
        filter.setStarred(false);

        filter.addCategory("Food")
                .addCategory("Travel")
                .addCategory("Groceries");

        filter.addPaymentMethod("Chase")
                .addPaymentMethod("amazon");
        Assert.assertEquals(Expenses.getForFilter(filter).size(),
                mExpenseDao.getForFilter(filter).size());

        filter = new Filter(timeZone);
        filter.addYear(2020);
        filter.addMonth(5);
        filter.addMonth(4);
        filter.setStarred(false);
        filter.addCategory("Utilities");
        filter.addPaymentMethod("Card4");
        Assert.assertEquals(Expenses.getForFilter(filter).size(),
                mExpenseDao.getForFilter(filter).size());
    }

    @Test
    public void testGetStores() {
        String startsWith = "PN";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());

        startsWith = "pn";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());

        startsWith = "CO";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());

        startsWith = "zx";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());

        startsWith = "H";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());

        startsWith = "Jess";
        Assert.assertEquals(Expenses.getStores(startsWith).size(),
                mExpenseDao.getStores(startsWith).size());
    }

    @Test
    public void testGetEntities() {
        String store = "Publix";

        Assert.assertNotNull(Expenses.getForStore(store));
        Assert.assertNotNull(mExpenseDao.getForStore(store));
        Assert.assertEquals(Objects.requireNonNull(Expenses.getForStore(store)).getCategory(),
                mExpenseDao.getForStore(store).getCategory());
        Assert.assertEquals(Objects.requireNonNull(Expenses.getForStore(store)).getPaymentMethod(),
                mExpenseDao.getForStore(store).getPaymentMethod());

        store = "publix";

        Assert.assertNotNull(Expenses.getForStore(store));
        Assert.assertNotNull(mExpenseDao.getForStore(store));
        Assert.assertEquals(Objects.requireNonNull(Expenses.getForStore(store)).getCategory(),
                mExpenseDao.getForStore(store).getCategory());
        Assert.assertEquals(Objects.requireNonNull(Expenses.getForStore(store)).getPaymentMethod(),
                mExpenseDao.getForStore(store).getPaymentMethod());

        store = "Root down";

        Assert.assertNull(Expenses.getForStore(store));
        Assert.assertNull(mExpenseDao.getForStore(store));
    }

    @Test
    public void testGetForRecord() {
        Assert.assertEquals(Expenses.getForRecordType(RecordType.ANNUAL).size(),
                mExpenseDao.getForRecordType(RecordType.ANNUAL).size());
        Assert.assertEquals(Expenses.getForRecordType(RecordType.MONTHLY).size(),
                mExpenseDao.getForRecordType(RecordType.MONTHLY).size());
    }

    @Test
    public void testUpdateSetIdentifier() {
        Assert.assertEquals(Expenses.getForEmptyIdentifier().size(),
                mExpenseDao.getForEmptyIdentifier().size());
        Assert.assertNotEquals(0, mExpenseDao.getForEmptyIdentifier().size());
        mExpenseDao.updateSetIdentifier();
        Assert.assertEquals(0, mExpenseDao.getForEmptyIdentifier().size());
    }
}

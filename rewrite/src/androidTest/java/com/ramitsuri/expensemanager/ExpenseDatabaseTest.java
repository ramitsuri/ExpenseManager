package com.ramitsuri.expensemanager;

import android.util.Log;

import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.dao.ExpenseDao;
import com.ramitsuri.expensemanager.entities.Expense;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExpenseDatabaseTest extends BaseDatabaseTest {

    private ExpenseDao mExpenseDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mExpenseDao = mDb.expenseDao();

        for (Expense expense : DummyData.getExpenses()) {
            mExpenseDao.insert(expense);
        }
    }

    @Test
    public void testGetAll() throws Exception {
        // Get all
        Assert.assertEquals(DummyData.getExpenses().size(), mExpenseDao.getAll().size());
        Log.d(TAG, mExpenseDao.getAll().toString());
    }

    @Test
    public void testGetAllStarred() throws Exception {
        // Get all starred
        Assert.assertEquals(
                DummyData.getAllStarred().size(),
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void testGetAllUnsynced() throws Exception {
        // get All unsynced
        Assert.assertEquals(
                DummyData.getAllUnsynced().size(),
                mExpenseDao.getAllUnsynced().size());
    }

    @Test
    public void testGetAllForBackup() throws Exception {
        // get All for backup
        List<Integer> monthIndices = new ArrayList<>();
        monthIndices.add(6);
        monthIndices.add(5);
        monthIndices.add(4);
        Assert.assertEquals(
                DummyData.getAllForBackup(monthIndices).size(),
                mExpenseDao.getAllForBackup(monthIndices).size());

        monthIndices = new ArrayList<>();
        monthIndices.add(0);
        Assert.assertEquals(
                DummyData.getAllForBackup(monthIndices).size(),
                mExpenseDao.getAllForBackup(monthIndices).size());

        monthIndices = new ArrayList<>();
        monthIndices.add(11);
        monthIndices.add(9);
        Assert.assertEquals(
                DummyData.getAllForBackup(monthIndices).size(),
                mExpenseDao.getAllForBackup(monthIndices).size());

        monthIndices = new ArrayList<>();
        Assert.assertEquals(
                DummyData.getAllForBackup(monthIndices).size(),
                mExpenseDao.getAllForBackup(monthIndices).size());
    }

    @Test
    public void testGetAllForDateRange() throws Exception {
        // get All within date range
        long start = DummyData.BASE_DATE_TIME;
        long end = DummyData.BASE_DATE_TIME + DummyData.ONE_DAY;
        Assert.assertEquals(DummyData.getAllForDateRange(start, end).size(),
                mExpenseDao.getAllForDateRange(start, end).size());
        start = DummyData.BASE_DATE_TIME - DummyData.ONE_DAY;
        end = DummyData.BASE_DATE_TIME + 2 * DummyData.ONE_DAY;
        Assert.assertEquals(DummyData.getAllForDateRange(start, end).size(),
                mExpenseDao.getAllForDateRange(start, end).size());
        start = DummyData.BASE_DATE_TIME - 3 * DummyData.ONE_DAY;
        end = DummyData.BASE_DATE_TIME + 3 * DummyData.ONE_DAY;
        Assert.assertEquals(DummyData.getAllForDateRange(start, end).size(),
                mExpenseDao.getAllForDateRange(start, end).size());
    }

    @Test
    public void testDeleteSynced() throws Exception {
        // delete synced
        mExpenseDao.deleteSynced();
        Assert.assertEquals(
                0,
                mExpenseDao.getAll().size() - mExpenseDao.getAllUnsynced().size());
    }

    @Test
    public void testDeleteAll() throws Exception {
        // delete all
        mExpenseDao.deleteAll();
        Assert.assertEquals(
                0,
                mExpenseDao.getAll().size());
    }

    @Test
    public void testAddAll() throws Exception {
        // add all
        for (Expense expense : DummyData.getExpenses()) {
            mExpenseDao.insert(expense);
        }
        Assert.assertEquals(
                DummyData.getExpenses().size(),
                mExpenseDao.getAll().size());
    }

    @Test
    public void testUpdateUnsynced() throws Exception {
        // update unsynced
        mExpenseDao.updateUnsynced();
        Assert.assertEquals(
                0,
                mExpenseDao.getAllUnsynced().size());
    }

    @Test
    public void testSetStarred() throws Exception {
        // set starred
        List<Expense> expenses = mExpenseDao.getAll();
        for (Expense expense : expenses) {
            if (!expense.isStarred()) {
                mExpenseDao.setStarred(expense.getId());
                break;
            }
        }
        Assert.assertEquals(
                DummyData.getAllStarred().size() + 1,
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void testSetUnstarred() throws Exception {
        // set unstarred
        mExpenseDao.setUnstarred(mExpenseDao.getAll().get(3).getId());
        Assert.assertEquals(
                DummyData.getAllStarred().size(),
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void testUpdateSetAllUnsynced() {
        Assert.assertNotEquals(mExpenseDao.getAllUnsynced().size(), mExpenseDao.getAll().size());

        mExpenseDao.updateSetAllUnsynced();
        Assert.assertEquals(mExpenseDao.getAllUnsynced().size(), mExpenseDao.getAll().size());
    }
}

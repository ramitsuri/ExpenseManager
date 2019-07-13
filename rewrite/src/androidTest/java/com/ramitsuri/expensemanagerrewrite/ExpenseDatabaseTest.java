package com.ramitsuri.expensemanagerrewrite;

import android.content.Context;
import android.util.Log;

import com.ramitsuri.expensemanagerrewrite.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.dao.ExpenseDao;
import com.ramitsuri.expensemanagerrewrite.dao.PaymentMethodDao;
import com.ramitsuri.expensemanagerrewrite.db.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExpenseDatabaseTest {

    private static final String TAG = ExpenseDatabaseTest.class.getName();

    private ExpenseDao mExpenseDao;
    private CategoryDao mCategoryDao;
    private PaymentMethodDao mPaymentMethodDao;
    private ExpenseManagerDatabase mDb;

    @Before
    public void createDb() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(appContext, ExpenseManagerDatabase.class).build();
        mExpenseDao = mDb.expenseDao();
        mCategoryDao = mDb.categoryDao();
        mPaymentMethodDao = mDb.paymentMethodDao();

        for (Expense expense : TestUtils.getExpenses()) {
            mExpenseDao.insert(expense);
        }

        mCategoryDao.insertAll(TestUtils.getAllCategories());

        mPaymentMethodDao.insertAll(TestUtils.getAllPaymentMethods());
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void expenseTest() throws Exception {
        // Get all
        Assert.assertEquals(TestUtils.getExpenses().size(), mExpenseDao.getAll().size());
        Log.d(TAG, mExpenseDao.getAll().toString());

        // Get all starred
        Assert.assertEquals(TestUtils.getAllStarred().size(), mExpenseDao.getAllStarred().size());

        // get All unsynced
        Assert.assertEquals(TestUtils.getAllUnsynced().size(), mExpenseDao.getAllUnsynced().size());

        // delete synced
        mExpenseDao.deleteSynced();
        Assert.assertEquals(0, mExpenseDao.getAll().size() - mExpenseDao.getAllUnsynced().size());

        // delete all
        mExpenseDao.deleteAll();
        Assert.assertEquals(0, mExpenseDao.getAll().size());

        // add all
        for (Expense expense : TestUtils.getExpenses()) {
            mExpenseDao.insert(expense);
        }
        Assert.assertEquals(TestUtils.getExpenses().size(), mExpenseDao.getAll().size());

        // update unsynced
        mExpenseDao.updateUnsynced();
        Assert.assertEquals(0, mExpenseDao.getAllUnsynced().size());

        // set starred
        mExpenseDao.setStarred(mExpenseDao.getAll().get(3).getId());
        Assert.assertEquals(TestUtils.getAllStarred().size() + 1,
                mExpenseDao.getAllStarred().size());

        // set unstarred
        mExpenseDao.setUnstarred(mExpenseDao.getAll().get(3).getId());
        Assert.assertEquals(TestUtils.getAllStarred().size(), mExpenseDao.getAllStarred().size());
    }

    @Test
    public void categoryTest() {
        // get all
        Assert.assertEquals(TestUtils.getCategories().length, mCategoryDao.getAll().size());
        Log.d(TAG, mCategoryDao.getAll().toString());

        // delete all
        mCategoryDao.deleteAll();
        Assert.assertEquals(0, mCategoryDao.getAll().size());
    }

    @Test
    public void paymentMethodTest() {
        // get all
        Assert.assertEquals(TestUtils.getPaymentMethods().length,
                mPaymentMethodDao.getAll().size());
        Log.d(TAG, mPaymentMethodDao.getAll().toString());

        // delete all
        mPaymentMethodDao.deleteAll();
        Assert.assertEquals(0, mPaymentMethodDao.getAll().size());
    }
}

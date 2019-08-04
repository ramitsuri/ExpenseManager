package com.ramitsuri.expensemanagerrewrite;

import android.content.Context;
import android.util.Log;

import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.data.DummyData;
import com.ramitsuri.expensemanagerrewrite.data.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.data.dao.ExpenseDao;
import com.ramitsuri.expensemanagerrewrite.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExpenseDatabaseTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final String TAG = ExpenseDatabaseTest.class.getName();

    private ExpenseDao mExpenseDao;
    private CategoryDao mCategoryDao;
    private PaymentMethodDao mPaymentMethodDao;
    private ExpenseManagerDatabase mDb;

    @Before
    public void createDb() {
        Context appContext = getContext();
        mDb = Room.inMemoryDatabaseBuilder(appContext, ExpenseManagerDatabase.class).build();
        mExpenseDao = mDb.expenseDao();
        mCategoryDao = mDb.categoryDao();
        mPaymentMethodDao = mDb.paymentMethodDao();

        for (Expense expense : DummyData.getExpenses()) {
            mExpenseDao.insert(expense);
        }

        mCategoryDao.insertAll(DummyData.getAllCategories());

        mPaymentMethodDao.insertAll(DummyData.getAllPaymentMethods());
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    private Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void expenseTest() throws Exception {
        // Get all
        Assert.assertEquals(DummyData.getExpenses().size(),
                LiveDataTestUtil.getValue(mExpenseDao.getAll()).size());
        Log.d(TAG, mExpenseDao.getAll().toString());

        // Get all starred
        Assert.assertEquals(
                DummyData.getAllStarred().size(),
                mExpenseDao.getAllStarred().size());

        // get All unsynced
        Assert.assertEquals(
                DummyData.getAllUnsynced().size(),
                mExpenseDao.getAllUnsynced().size());

        // delete synced
        mExpenseDao.deleteSynced();
        Assert.assertEquals(
                0,
                LiveDataTestUtil.getValue(mExpenseDao.getAll()).size() -
                        mExpenseDao.getAllUnsynced().size());

        // delete all
        mExpenseDao.deleteAll();
        Assert.assertEquals(
                0,
                LiveDataTestUtil.getValue(mExpenseDao.getAll()).size());

        // add all
        for (Expense expense : DummyData.getExpenses()) {
            mExpenseDao.insert(expense);
        }
        Assert.assertEquals(
                DummyData.getExpenses().size(),
                LiveDataTestUtil.getValue(mExpenseDao.getAll()).size());

        // update unsynced
        mExpenseDao.updateUnsynced();
        Assert.assertEquals(
                0,
                mExpenseDao.getAllUnsynced().size());

        // set starred
        mExpenseDao.setStarred(LiveDataTestUtil.getValue(mExpenseDao.getAll()).get(3).getId());
        Assert.assertEquals(
                DummyData.getAllStarred().size() + 1,
                mExpenseDao.getAllStarred().size());

        // set unstarred
        mExpenseDao.setUnstarred(LiveDataTestUtil.getValue(mExpenseDao.getAll()).get(3).getId());
        Assert.assertEquals(
                DummyData.getAllStarred().size(),
                mExpenseDao.getAllStarred().size());
    }

    @Test
    public void categoryTest() throws Exception {
        // get all
        Assert.assertEquals(
                DummyData.getCategories().length,
                mCategoryDao.getAll().size());
        Log.d(TAG, mCategoryDao.getAll().toString());

        // delete all
        mCategoryDao.deleteAll();
        Assert.assertEquals(
                0,
                mCategoryDao.getAll().size());

        // set all
        mCategoryDao.setAll(DummyData.getAllCategories());
        Assert.assertEquals(
                DummyData.getCategories().length,
                mCategoryDao.getAll().size());
    }

    @Test
    public void paymentMethodTest() throws Exception {
        // get all
        Assert.assertEquals(
                DummyData.getPaymentMethods().length,
                mPaymentMethodDao.getAll().size());
        Log.d(TAG, mPaymentMethodDao.getAll().toString());

        // delete all
        mPaymentMethodDao.deleteAll();
        Assert.assertEquals(
                0,
                mPaymentMethodDao.getAll().size());

        // set all
        mPaymentMethodDao.setAll(DummyData.getAllPaymentMethods());
        Assert.assertEquals(
                DummyData.getPaymentMethods().length,
                mPaymentMethodDao.getAll().size());
    }
}

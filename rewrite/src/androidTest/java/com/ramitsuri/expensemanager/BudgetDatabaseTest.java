package com.ramitsuri.expensemanager;

import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.dao.BudgetDao;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class BudgetDatabaseTest extends BaseDatabaseTest {
    private BudgetDao mBudgetDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mBudgetDao = mDb.budgetDao();

        mBudgetDao.setAll(DummyData.getBudgets());
    }

    @Test
    public void testBudgets() {
        Assert.assertEquals(DummyData.getBudgets().size(), mBudgetDao.getAll().size());

        mBudgetDao.updateCategory("Food", "Foods");
        boolean contains = false;
        for (Budget budget : mBudgetDao.getAll()) {
            contains = ObjectHelper.contains(budget.getCategories(), "Foods");
            if (contains) {
                break;
            }
        }
        Assert.assertTrue(contains);

        mBudgetDao.deleteCategory("Food");
        contains = false;
        for (Budget budget : mBudgetDao.getAll()) {
            contains = ObjectHelper.contains(budget.getCategories(), "Food");
            if (contains) {
                break;
            }
        }
        Assert.assertFalse(contains);

        mBudgetDao.deleteCategory("Foods");
        contains = false;
        for (Budget budget : mBudgetDao.getAll()) {
            contains = ObjectHelper.contains(budget.getCategories(), "Foods");
            if (contains) {
                break;
            }
        }
        Assert.assertTrue(contains);

        mBudgetDao.deleteAll();
        Assert.assertEquals(0, mBudgetDao.getAll().size());
    }
}

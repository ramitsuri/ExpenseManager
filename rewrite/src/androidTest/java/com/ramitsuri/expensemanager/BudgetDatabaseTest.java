package com.ramitsuri.expensemanager;

import com.ramitsuri.expensemanager.data.dao.BudgetDao;
import com.ramitsuri.expensemanager.data.dummy.Budgets;
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

        mBudgetDao.setAll(Budgets.getBudgets());
    }

    @Test
    public void testUpdateCategory() {
        Assert.assertEquals(Budgets.getBudgets().size(), mBudgetDao.getAll().size());

        mBudgetDao.updateCategory("Food", "Foods");
        boolean contains = false;
        for (Budget budget : mBudgetDao.getAll()) {
            contains = ObjectHelper.contains(budget.getCategories(), "Foods");
            if (contains) {
                break;
            }
        }
        Assert.assertTrue(contains);
    }

    @Test
    public void testDeleteCategory() {
        mBudgetDao.deleteCategory("Food");
        boolean contains = false;
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
        Assert.assertFalse(contains);
    }

    @Test
    public void testDeleteAll() {
        mBudgetDao.deleteAll();
        Assert.assertEquals(0, mBudgetDao.getAll().size());
    }
}

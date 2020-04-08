package com.ramitsuri.expensemanager;

import android.util.Log;

import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.dao.CategoryDao;

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
public class CategoryDatabaseTest extends BaseDatabaseTest {
    private CategoryDao mCategoryDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mCategoryDao = mDb.categoryDao();

        mCategoryDao.insertAll(DummyData.getAllCategories());
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
}

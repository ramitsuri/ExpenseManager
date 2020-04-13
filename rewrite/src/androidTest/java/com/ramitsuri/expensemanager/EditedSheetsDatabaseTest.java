package com.ramitsuri.expensemanager;

import android.database.sqlite.SQLiteConstraintException;

import com.ramitsuri.expensemanager.data.dao.EditedSheetDao;
import com.ramitsuri.expensemanager.data.dummy.EditedSheets;
import com.ramitsuri.expensemanager.entities.EditedSheet;

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
public class EditedSheetsDatabaseTest extends BaseDatabaseTest {

    private EditedSheetDao mEditedSheetDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();

        mEditedSheetDao = mDb.editedSheetDao();

        for (EditedSheet editedSheet : EditedSheets.getEditedSheets()) {
            mEditedSheetDao.insert(editedSheet);
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void editedSheetsTest() {
        // 1
        Assert.assertEquals(EditedSheets.getEditedSheets().size(), mEditedSheetDao.getAll().size());

        // 2
        mEditedSheetDao.deleteAll();
        Assert.assertEquals(0, mEditedSheetDao.getAll().size());

        // 3
        for (EditedSheet editedSheet : EditedSheets.getEditedSheets()) {
            mEditedSheetDao.insert(editedSheet);
        }
        // Will throw exception
        mEditedSheetDao.insert(EditedSheets.getEditedSheets().get(0));
    }
}

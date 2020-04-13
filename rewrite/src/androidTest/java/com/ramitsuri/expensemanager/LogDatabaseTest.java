package com.ramitsuri.expensemanager;

import com.ramitsuri.expensemanager.data.dao.LogDao;
import com.ramitsuri.expensemanager.data.dummy.Logs;

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
public class LogDatabaseTest extends BaseDatabaseTest {

    private LogDao mLogDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mLogDao = mDb.logDao();

        for (com.ramitsuri.expensemanager.entities.Log log : Logs.getLogs()) {
            mLogDao.insert(log);
        }
    }

    @Test
    public void logsTest() {
        Assert.assertEquals(Logs.getUnacknowledgedLogs().size(),
                mLogDao.getUnacknowledged().size());
    }

}

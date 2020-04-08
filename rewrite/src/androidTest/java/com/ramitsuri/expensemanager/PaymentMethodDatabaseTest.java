package com.ramitsuri.expensemanager;

import android.util.Log;

import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.dao.PaymentMethodDao;

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
public class PaymentMethodDatabaseTest extends BaseDatabaseTest {

    private PaymentMethodDao mPaymentMethodDao;

    @Before
    @Override
    public void createDb() {
        super.createDb();
        mPaymentMethodDao = mDb.paymentMethodDao();
        mPaymentMethodDao.insertAll(DummyData.getAllPaymentMethods());
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

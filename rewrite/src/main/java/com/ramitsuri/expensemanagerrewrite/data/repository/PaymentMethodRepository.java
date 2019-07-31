package com.ramitsuri.expensemanagerrewrite.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

public class PaymentMethodRepository {
    private PaymentMethodDao mDao;
    private LiveData<List<PaymentMethod>> mPaymentMethods;

    public PaymentMethodRepository(Application application) {
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance(application);
        mDao = database.paymentMethodDao();
        mPaymentMethods = mDao.getAll();
    }

    public LiveData<List<PaymentMethod>> getPaymentMethods() {
        return mPaymentMethods;
    }

    public void setPaymentMethods(String[] paymentMethods) {
        new insertAsyncTask(mDao).execute(paymentMethods);
    }

    private static class insertAsyncTask extends AsyncTask<String[], Void, Void> {

        private PaymentMethodDao mAsyncTaskDao;

        insertAsyncTask(PaymentMethodDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String[]... args) {
            List<PaymentMethod> paymentMethods = new ArrayList<>();
            for (String paymentMethodName : args[0]) {
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setName(paymentMethodName);
                paymentMethods.add(paymentMethod);
            }
            mAsyncTaskDao.setAll(paymentMethods);
            return null;
        }
    }
}

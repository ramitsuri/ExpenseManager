package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.IntDefs.SourceType;
import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PaymentMethodRepository {

    @SourceType
    private int mSourceType;
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public PaymentMethodRepository(AppExecutors executors, ExpenseManagerDatabase database,
            @SourceType int sourceType) {
        mExecutors = executors;
        mDatabase = database;
        mSourceType = SourceType.DB;
    }

    public LiveData<List<PaymentMethod>> getPaymentMethods() {
        final MutableLiveData<List<PaymentMethod>> paymentMethods = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<PaymentMethod> values = null;
                if (mSourceType == SourceType.LOCAL) {
                    values = DummyData.getAllPaymentMethods();
                } else if (mSourceType == SourceType.DB) {
                    values = mDatabase.paymentMethodDao().getAll();
                }
                paymentMethods.postValue(values);
            }
        });
        return paymentMethods;
    }

    public void setPaymentMethods(final List<String> paymentMethods) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<PaymentMethod> paymentMethodList = new ArrayList<>();
                for (String paymentMethodName : paymentMethods) {
                    PaymentMethod paymentMethod = new PaymentMethod();
                    paymentMethod.setName(paymentMethodName);
                    paymentMethodList.add(paymentMethod);
                }
                mDatabase.paymentMethodDao().setAll(paymentMethodList);
            }
        });
    }
}
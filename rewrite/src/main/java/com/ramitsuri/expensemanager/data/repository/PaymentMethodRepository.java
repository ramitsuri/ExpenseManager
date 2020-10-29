package com.ramitsuri.expensemanager.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodRepository extends BaseRepository {

    public PaymentMethodRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        super(executors, database);
    }

    public LiveData<List<PaymentMethod>> getPaymentMethods() {
        final MutableLiveData<List<PaymentMethod>> paymentMethods = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<PaymentMethod> values = mDatabase.paymentMethodDao().getAll();
                paymentMethods.postValue(values);
            }
        });
        return paymentMethods;
    }

    public MutableLiveData<List<String>> getPaymentMethodStrings() {
        final MutableLiveData<List<String>> paymentMethods = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<PaymentMethod> values = mDatabase.paymentMethodDao().getAll();
                List<String> stringValues = new ArrayList<>();
                for (PaymentMethod value : values) {
                    stringValues.add(value.getName());
                }
                paymentMethods.postValue(stringValues);
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

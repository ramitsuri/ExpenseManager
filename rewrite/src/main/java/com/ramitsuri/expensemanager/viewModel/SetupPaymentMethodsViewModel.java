package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupPaymentMethodsViewModel extends ViewModel {
    private MutableLiveData<List<String>> mPaymentMethodsLive;

    public SetupPaymentMethodsViewModel() {
        super();

        mPaymentMethodsLive = paymentMethodRepo().getPaymentMethodStrings();
    }

    public LiveData<List<String>> getPaymentMethodsLive() {
        return mPaymentMethodsLive;
    }

    public boolean addPaymentMethod(@Nonnull String value) {
        List<String> values = mPaymentMethodsLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        if (!values.contains(value)) {
            values.add(value);
            mPaymentMethodsLive.postValue(values);
            return true;
        }
        return false;
    }

    public boolean editPaymentMethod(@Nonnull String oldValue, @Nonnull String newValue) {
        List<String> values = mPaymentMethodsLive.getValue();
        if (values == null) {
            return false;
        }
        if (values.contains(newValue)) {
            return false;
        }
        if (values.contains(oldValue)) {
            int index = values.indexOf(oldValue);
            values.remove(index);
            values.add(index, newValue);
            mPaymentMethodsLive.postValue(values);
            return true;
        }
        return false;
    }

    public boolean deletePaymentMethod(@Nonnull String value) {
        List<String> values = mPaymentMethodsLive.getValue();
        if (values == null) {
            return false;
        }
        if (values.size() == 1) {
            return false;
        }
        if (values.contains(value)) {
            values.remove(value);
            mPaymentMethodsLive.postValue(values);
            return true;
        }
        return false;
    }

    public void savePaymentMethods() {
        if (paymentMethodRepo() != null) {
            List<String> values = mPaymentMethodsLive.getValue();
            if (values != null) {
                paymentMethodRepo().setPaymentMethods(values);
            }
        }
    }

    private PaymentMethodRepository paymentMethodRepo() {
        return MainApplication.getInstance().getPaymentMethodRepo();
    }
}

package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupPaymentMethodsViewModel extends ViewModel {

    private MutableLiveData<List<String>> mValuesLive;
    private boolean mChangesMade;

    public SetupPaymentMethodsViewModel() {
        super();

        mValuesLive = repository().getPaymentMethodStrings();
    }

    public LiveData<List<String>> getValuesLive() {
        return mValuesLive;
    }

    public boolean add(@Nonnull String value) {
        List<String> values = mValuesLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        if (!ObjectHelper.contains(values, value)) {
            values.add(value);
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public boolean edit(@Nonnull String oldValue, @Nonnull String newValue) {
        List<String> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }
        if (ObjectHelper.contains(values, newValue)) {
            return false;
        }
        if (ObjectHelper.contains(values, oldValue)) {
            int index = values.indexOf(oldValue);
            values.remove(index);
            values.add(index, newValue);
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public boolean delete(@Nonnull String value) {
        List<String> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }
        if (values.size() == 1) {
            return false;
        }
        if (ObjectHelper.contains(values, value)) {
            values.remove(value);
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public void save() {
        if (!mChangesMade) {
            return;
        }
        if (repository() != null) {
            List<String> values = mValuesLive.getValue();
            if (values != null) {
                repository().setPaymentMethods(values);
                // Entities have been edited
                AppHelper.setEntitiesEdited(true);
            }
        }
    }

    private PaymentMethodRepository repository() {
        return MainApplication.getInstance().getPaymentMethodRepo();
    }
}

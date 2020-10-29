package com.ramitsuri.expensemanager.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.ui.adapter.ListOptionsItemWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SetupPaymentMethodsViewModel extends ViewModel {

    private MutableLiveData<List<String>> mValuesLive;
    private boolean mChangesMade;

    public SetupPaymentMethodsViewModel() {
        super();

        mValuesLive = repository().getPaymentMethodStrings();
    }

    public LiveData<List<ListOptionsItemWrapper>> getValuesLive() {
        return Transformations
                .map(mValuesLive, new Function<List<String>, List<ListOptionsItemWrapper>>() {
                    @Override
                    public List<ListOptionsItemWrapper> apply(List<String> input) {
                        List<ListOptionsItemWrapper> wrappers = new ArrayList<>();
                        for (String value : input) {
                            wrappers.add(new ListOptionsItemWrapper(value));
                        }
                        return wrappers;
                    }
                });
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
                WorkHelper.enqueueOneTimeEntitiesBackup(true);
            }
        }
    }

    private PaymentMethodRepository repository() {
        return MainApplication.getInstance().getPaymentMethodRepo();
    }
}

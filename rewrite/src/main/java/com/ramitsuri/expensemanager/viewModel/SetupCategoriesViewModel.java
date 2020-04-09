package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupCategoriesViewModel extends ViewModel {

    private MutableLiveData<List<String>> mValuesLive;
    private boolean mChangesMade;
    private Map<String, String> mEditedCategories;

    public SetupCategoriesViewModel() {
        super();

        mValuesLive = repository().getCategoryStrings();
        mEditedCategories = new HashMap<>();
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
            updateEditedCategories(oldValue, newValue);
            return true;
        }
        return false;
    }

    private void updateEditedCategories(@Nonnull String oldValue, @Nonnull String newValue) {
        //if(mEditedCategories.containsKey(oldValue))
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
                repository().setCategories(values);
                // Entities have been edited
                AppHelper.setEntitiesEdited(true);
            }
        }
    }

    private CategoryRepository repository() {
        return MainApplication.getInstance().getCategoryRepo();
    }

    private BudgetRepository budgetRepository() {
        return MainApplication.getInstance().getBudgetRepository();
    }
}

package com.ramitsuri.expensemanager.viewModel;

import android.util.Pair;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupCategoriesViewModel extends ViewModel {

    private MutableLiveData<List<String>> mValuesLive;
    private boolean mChangesMade;
    private List<Pair<String, String>> mEditedCategories;

    public SetupCategoriesViewModel() {
        super();

        mValuesLive = repository().getCategoryStrings();
        mEditedCategories = new ArrayList<>();
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
            mEditedCategories.add(new Pair<>(value, (String)null));
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
        if (budgetRepository() != null) {
            if (mEditedCategories.size() > 0) {
                budgetRepository().updateCategories(mEditedCategories);
            }
        }
    }

    private CategoryRepository repository() {
        return MainApplication.getInstance().getCategoryRepo();
    }

    private BudgetRepository budgetRepository() {
        return MainApplication.getInstance().getBudgetRepository();
    }

    private void updateEditedCategories(@Nonnull String oldValue, @Nonnull String newValue) {
        mEditedCategories.add(new Pair<>(oldValue, newValue));
    }
}

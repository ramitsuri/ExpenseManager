package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupCategoriesViewModel extends ViewModel {

    private MutableLiveData<List<String>> mCategoriesLive;

    public SetupCategoriesViewModel() {
        super();

        mCategoriesLive = categoryRepo().getCategoryStrings();
    }

    public LiveData<List<String>> getCategoriesLive() {
        return mCategoriesLive;
    }

    public boolean addCategory(@Nonnull String value) {
        List<String> values = mCategoriesLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        if (!values.contains(value)) {
            values.add(value);
            mCategoriesLive.postValue(values);
            return true;
        }
        return false;
    }

    public boolean editCategory(@Nonnull String oldValue, @Nonnull String newValue) {
        List<String> values = mCategoriesLive.getValue();
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
            mCategoriesLive.postValue(values);
            return true;
        }
        return false;
    }

    public boolean deleteCategory(@Nonnull String value) {
        List<String> values = mCategoriesLive.getValue();
        if (values == null) {
            return false;
        }
        if (values.size() == 1) {
            return false;
        }
        if (values.contains(value)) {
            values.remove(value);
            mCategoriesLive.postValue(values);
            return true;
        }
        return false;
    }

    public void saveCategories() {
        if (categoryRepo() != null) {
            List<String> values = mCategoriesLive.getValue();
            if (values != null) {
                categoryRepo().setCategories(values);
            }
        }
    }

    private CategoryRepository categoryRepo() {
        return MainApplication.getInstance().getCategoryRepo();
    }
}

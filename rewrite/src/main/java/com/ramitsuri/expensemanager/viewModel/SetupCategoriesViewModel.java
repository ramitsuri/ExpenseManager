package com.ramitsuri.expensemanager.viewModel;

import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.ui.adapter.ListItemWrapper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SetupCategoriesViewModel extends ViewModel {

    @Nonnull
    private final MutableLiveData<List<Category>> mValuesLive;
    private boolean mChangesMade;
    @Nonnull
    private final List<Pair<String, String>> mEditedCategories;
    @Nonnull
    @RecordType
    private String mSelectedRecordType;

    public SetupCategoriesViewModel() {
        super();
        mSelectedRecordType = RecordType.MONTHLY;
        repository().getAll();
        mValuesLive = repository().getCategories();
        mEditedCategories = new ArrayList<>();
    }

    public void onMonthlyTabSelected() {
        mSelectedRecordType = RecordType.MONTHLY;
    }

    public void onAnnualTabSelected() {
        mSelectedRecordType = RecordType.ANNUAL;
    }

    public List<ListItemWrapper> getValues() {
        List<Category> categories = mValuesLive.getValue();
        List<ListItemWrapper> wrappers = new ArrayList<>();
        if (categories != null) {
            for (Category category : categories) {
                if (mSelectedRecordType.equals(category.getRecordType())) {
                    wrappers.add(new ListItemWrapper(category.getName())
                            .setRecordType(mSelectedRecordType));
                }
            }
        }
        return wrappers;
    }

    public LiveData<Boolean> areValuesLoaded() {
        return Transformations
                .map(mValuesLive, new Function<List<Category>, Boolean>() {
                    @Override
                    public Boolean apply(List<Category> input) {
                        return input != null && input.size() > 0;
                    }
                });
    }

    public boolean add(@Nonnull String value) {
        List<Category> values = mValuesLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }

        // TODO consider if need to allow adding same categories with different record types
        if (ObjectHelper.indexOf(values, value) == -1) { // Not contains
            values.add(new Category(value, mSelectedRecordType));
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public boolean edit(@Nonnull String oldValue, @Nonnull String newValue) {
        List<Category> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }

        // TODO consider if need to allow adding same categories with different record types
        if (ObjectHelper.indexOf(values, newValue) != -1) { // Contains new value already
            return false;
        }
        int indexOldValue = ObjectHelper.indexOf(values, oldValue);
        if (indexOldValue != -1) { // Contains old value
            values.remove(indexOldValue);
            values.add(indexOldValue, new Category(newValue, mSelectedRecordType));
            mValuesLive.postValue(values);
            mChangesMade = true;
            updateEditedCategories(oldValue, newValue);
            return true;
        }
        return false;
    }

    public boolean delete(@Nonnull String value) {
        List<Category> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }
        if (values.size() == 1) {
            return false;
        }

        int index = ObjectHelper.indexOf(values, value);
        if (index != -1) { // Contains
            values.remove(index);
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
        List<Category> values = mValuesLive.getValue();
        if (values != null) {
            repository().setCategories(values);
        }
        if (mEditedCategories.size() > 0) {
            budgetRepository().updateCategories(mEditedCategories);
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

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
import com.ramitsuri.expensemanager.ui.adapter.ListOptionsItemWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SetupCategoriesViewModel extends ViewModel {

    @Nonnull
    private MutableLiveData<List<Category>> mValuesLive;
    private boolean mChangesMade;
    @Nonnull
    private List<Pair<String, String>> mEditedCategories;
    @Nonnull
    @RecordType
    private String mSelectedRecordType;

    public SetupCategoriesViewModel() {
        super();
        mSelectedRecordType = RecordType.MONTHLY;
        mValuesLive = repository().getCategories();
        mEditedCategories = new ArrayList<>();
    }

    public void onMonthlyTabSelected() {
        mSelectedRecordType = RecordType.MONTHLY;
    }

    public void onAnnualTabSelected() {
        mSelectedRecordType = RecordType.ANNUAL;
    }

    public List<ListOptionsItemWrapper> getValues() {
        List<Category> categories = mValuesLive.getValue();
        List<ListOptionsItemWrapper> wrappers = new ArrayList<>();
        if (categories != null) {
            for (Category category : categories) {
                if (mSelectedRecordType.equals(category.getRecordType())) {
                    wrappers.add(new ListOptionsItemWrapper(category.getName())
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
        if (contains(values, value) == -1) { // Not contains
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
        if (contains(values, newValue) != -1) { // Contains new value already
            return false;
        }
        int indexOldValue = contains(values, oldValue);
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
        int index = contains(values, value);
        if (index != -1) { // Contains
            values.remove(index);
            mValuesLive.postValue(values);
            mChangesMade = true;
            mEditedCategories.add(new Pair<>(value, (String) null));
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
            // Entities have been edited
            AppHelper.setEntitiesEdited(true);
            WorkHelper.enqueueOneTimeEntitiesBackup(true);
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

    // TODO consider if need to allow adding same categories with different record types
    private int contains(@Nonnull List<Category> categories, @Nonnull String value) {
        int index = -1;
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            if (value.equalsIgnoreCase(category.getName())) {
                index = i;
                break;
            }
        }
        return index;
    }
}

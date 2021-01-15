package com.ramitsuri.expensemanager.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.ui.adapter.BudgetCategoryWrapper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetupBudgetsViewModel extends ViewModel {

    private MutableLiveData<List<Budget>> mValuesLive;
    private boolean mChangesMade;
    private List<Category> mAllCategories;
    @Nonnull
    @RecordType
    private String mSelectedRecordType;

    public SetupBudgetsViewModel() {
        super();

        mSelectedRecordType = RecordType.MONTHLY;
        mValuesLive = repository().getBudgetss();
    }

    public void onMonthlyTabSelected() {
        mSelectedRecordType = RecordType.MONTHLY;
    }

    public void onAnnualTabSelected() {
        mSelectedRecordType = RecordType.ANNUAL;
    }

    public LiveData<Boolean> areValuesLoaded() {
        return Transformations
                .map(mValuesLive, new Function<List<Budget>, Boolean>() {
                    @Override
                    public Boolean apply(List<Budget> input) {
                        return input != null && input.size() > 0;
                    }
                });
    }

    public List<Budget> getValues() {
        List<Budget> budgets = mValuesLive.getValue();
        List<Budget> budgetsForRecordType = new ArrayList<>();
        if (budgets != null) {
            for (Budget budget : budgets) {
                if (mSelectedRecordType.equals(budget.getRecordType())) {
                    budgetsForRecordType.add(budget);
                }
            }
        }
        return budgetsForRecordType;
    }

    public boolean add(@Nonnull Budget value) {
        value.setRecordType(mSelectedRecordType);
        List<Budget> values = mValuesLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        if (contains(values, value) == -1) { // Not contains
            values.add(value);
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public boolean edit(@Nonnull Budget oldValue, @Nonnull Budget newValue) {
        List<Budget> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }
        int indexOld = contains(values, oldValue);
        if (indexOld != -1) { // Contains old value
            values.remove(indexOld);
            values.add(indexOld, newValue);
            mValuesLive.postValue(values);
            mChangesMade = true;
            return true;
        }
        return false;
    }

    public boolean delete(@Nonnull Budget value) {
        List<Budget> values = mValuesLive.getValue();
        if (values == null) {
            return false;
        }
        int index = contains(values, value);
        if (index != -1) { // Contains
            values.remove(index);
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
        List<Budget> values = mValuesLive.getValue();
        if (values != null) {
            repository().setBudgets(values);
        }
    }

    @Nullable
    public ArrayList<BudgetCategoryWrapper> getCategoryWrappers(@Nullable Budget budget) {
        ArrayList<BudgetCategoryWrapper> categoryWrappers;
        List<String> usedCategories = getUsedCategories(mSelectedRecordType);
        if (mAllCategories == null || usedCategories == null) {
            return null;
        }
        List<Category> filteredCategories =
                filterForRecordType(mAllCategories, mSelectedRecordType);
        if (budget == null && filteredCategories.size() == usedCategories.size()) {
            // New budget requested but all categories already being used
            return null;
        }
        categoryWrappers = new ArrayList<>();
        for (Category category : mAllCategories) {
            if (!mSelectedRecordType.equalsIgnoreCase(category.getRecordType())) {
                // Show only the categories that correspond to selected record type
                continue;
            }
            BudgetCategoryWrapper wrapper = new BudgetCategoryWrapper(category.getName());
            if (budget != null && budget.getCategories() != null &&
                    ObjectHelper.contains(budget.getCategories(), category.getName())) {
                // Category already contained in budget being edited, mark it available and selected
                wrapper.setSelected(true);
                wrapper.setAvailable(true);
            } else if (ObjectHelper.contains(usedCategories, category.getName())) {
                // Category is being used in other budgets, mark unselected and unavailable
                wrapper.setSelected(false);
                wrapper.setAvailable(false);
            } else {
                // Category available and not selected
                wrapper.setSelected(false);
                wrapper.setAvailable(true);
            }
            categoryWrappers.add(wrapper);
        }
        return categoryWrappers;
    }

    @Nullable
    private List<String> getUsedCategories(@RecordType String recordType) {
        if (mAllCategories == null || mValuesLive.getValue() == null) {
            return null;
        }
        List<String> usedCategories = new ArrayList<>();
        for (Budget budget : mValuesLive.getValue()) {
            if (!recordType.equalsIgnoreCase(budget.getRecordType())) {
                continue;
            }
            for (String category : budget.getCategories()) {
                if (contains(mAllCategories, category) != -1) { // Contains
                    usedCategories.add(category);
                }
            }
        }
        return usedCategories;
    }

    public LiveData<Boolean> areCategoriesLoaded() {
        categoryRepository().getAll();
        return Transformations.map(categoryRepository().getCategories(),
                new Function<List<Category>, Boolean>() {
                    @Override
                    public Boolean apply(List<Category> input) {
                        mAllCategories = input;
                        return true;
                    }
                });
    }

    private BudgetRepository repository() {
        return MainApplication.getInstance().getBudgetRepository();
    }

    private CategoryRepository categoryRepository() {
        return MainApplication.getInstance().getCategoryRepo();
    }

    private int contains(@Nonnull List<Budget> values, @Nonnull Budget value) {
        int index = -1;
        for (int i = 0; i < values.size(); i++) {
            Budget budget = values.get(i);
            if (budget.getName().equalsIgnoreCase(value.getName())) {
                index = i;
                break;
            }
        }
        return index;
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

    private List<Category> filterForRecordType(List<Category> categories,
            @RecordType String recordType) {
        List<Category> filteredCategories = new ArrayList<>();
        for (Category category : categories) {
            if (recordType.equalsIgnoreCase(category.getRecordType())) {
                filteredCategories.add(category);
            }
        }
        return filteredCategories;
    }
}

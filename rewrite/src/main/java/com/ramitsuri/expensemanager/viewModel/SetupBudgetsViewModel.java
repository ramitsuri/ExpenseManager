package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.ui.adapter.BudgetCategoryWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class SetupBudgetsViewModel extends ViewModel {

    private MutableLiveData<List<Budget>> mValuesLive;
    private boolean mChangesMade;
    private List<String> mAllCategories;

    public SetupBudgetsViewModel() {
        super();

        mValuesLive = repository().getBudgetss();
    }

    public LiveData<List<Budget>> getValuesLive() {
        return mValuesLive;
    }

    public boolean add(@Nonnull Budget value) {
        List<Budget> values = mValuesLive.getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        if (!contains(values, value)) {
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
        if (contains(values, oldValue)) {
            int index = values.indexOf(oldValue);
            values.remove(index);
            values.add(index, newValue);
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
        if (contains(values, value)) {
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
            List<Budget> values = mValuesLive.getValue();
            if (values != null) {
                repository().setBudgets(values);
                // Entities have been edited
                AppHelper.setEntitiesEdited(true);
            }
        }
    }

    @Nullable
    public ArrayList<BudgetCategoryWrapper> getCategoryWrappers(@Nullable Budget budget) {
        ArrayList<BudgetCategoryWrapper> categoryWrappers = null;
        List<String> usedCategories = getUsedCategories();
        if (mAllCategories != null && usedCategories != null) {
            if (mAllCategories.size() != usedCategories.size()) {
                categoryWrappers = new ArrayList<>();
                for (String category : mAllCategories) {
                    BudgetCategoryWrapper wrapper = new BudgetCategoryWrapper(category);
                    if (budget != null && budget.getCategories() != null &&
                            ObjectHelper.contains(budget.getCategories(), category)) { // Contained in budget
                        wrapper.setSelected(true);
                        wrapper.setAvailable(true);
                    } else if (ObjectHelper.contains(usedCategories, category)) {
                        wrapper.setSelected(false);
                        wrapper.setAvailable(false);
                    } else {
                        wrapper.setSelected(false);
                        wrapper.setAvailable(true);
                    }
                    categoryWrappers.add(wrapper);
                }
            }
        }
        return categoryWrappers;
    }

    @Nullable
    private List<String> getUsedCategories() {
        if (mAllCategories == null || mValuesLive.getValue() == null) {
            return null;
        }
        List<String> usedCategories = new ArrayList<>();
        for (Budget budget : mValuesLive.getValue()) {
            for (String category : budget.getCategories()) {
                if (mAllCategories.contains(category)) {
                    usedCategories.add(category);
                }
            }
        }
        return usedCategories;
    }

    public LiveData<Boolean> areCategoriesLoaded() {
        return Transformations.map(categoryRepository().getCategoryStrings(),
                new Function<List<String>, Boolean>() {
                    @Override
                    public Boolean apply(List<String> input) {
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

    private boolean contains(@Nonnull List<Budget> values, @Nonnull Budget value) {
        for (Budget budget : values) {
            if (budget.getName().equalsIgnoreCase(value.getName()) &&
                    budget.getAmount().compareTo(value.getAmount()) == 0 &&
                    budget.getCategories().size() == value.getCategories().size()) {
                boolean contains = false;
                for (String category : value.getCategories()) {
                    contains = ObjectHelper.contains(budget.getCategories(), category);
                }
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }
}

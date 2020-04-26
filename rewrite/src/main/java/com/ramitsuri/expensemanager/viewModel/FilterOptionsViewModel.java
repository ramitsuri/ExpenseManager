package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.FilterWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.SecretMessageHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class FilterOptionsViewModel extends ViewModel {

    private Filter mFilter;
    private ExpenseRepository mExpenseRepo;

    private List<String> mCategories, mPayments;

    public FilterOptionsViewModel(@Nonnull Filter filter) {
        super();
        mFilter = filter;

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
    }

    public Filter clear() {
        mFilter
                .clear()
                .getDefault();
        return mFilter;
    }

    public Filter get() {
        return mFilter;
    }

    // region Months
    public List<FilterWrapper> getMonths() {
        List<FilterWrapper> wrappers = new ArrayList<>();
        int index = 0;
        for (String month : AppHelper.getMonths()) {
            boolean selected = mFilter.getDateTimes() != null &&
                    mFilter.getDateTimes().get(index) != null;
            wrappers.add(new FilterWrapper(month, selected));
            index = index + 1;
        }
        return wrappers;
    }

    public void onAddMonth(@Nonnull FilterWrapper value) {
        int index = getMonthPicked(value.getValue());
        mFilter
                .addMonthIndex(index);
    }

    public void onRemoveMonth(@Nonnull FilterWrapper value) {
        if (mFilter.getDateTimes() != null && mFilter.getDateTimes().size() > 1) {
            int index = getMonthPicked(value.getValue());
            mFilter
                    .removeMonthIndex(index);
        }
    }

    /**
     * Converts picked month into its corresponding index in the range 0 - 11
     */
    private int getMonthPicked(String pickedMonth) {
        int index = 0;
        for (String month : AppHelper.getMonths()) {
            if (pickedMonth.equalsIgnoreCase(month)) {
                break;
            }
            index = index + 1;
        }
        return index;
    }
    // endregion

    // region Categories
    public LiveData<Boolean> areCategoriesAvailable() {
        return Transformations.map(mExpenseRepo.getCategories(),
                new Function<List<String>, Boolean>() {
                    @Override
                    public Boolean apply(List<String> input) {
                        mCategories = input;
                        return input != null && input.size() > 0;
                    }
                });
    }

    public List<FilterWrapper> getCategories() {
        if (mCategories == null || mCategories.size() == 0) {
            return null;
        }
        List<FilterWrapper> wrappers = new ArrayList<>();
        for (String value : mCategories) {
            boolean selected = mFilter.getCategories() != null &&
                    mFilter.getCategories().contains(value);
            wrappers.add(new FilterWrapper(value, selected));
        }
        return wrappers;
    }

    public void onAddCategory(@Nonnull FilterWrapper value) {
        mFilter
                .addCategory(value.getValue());
    }

    public void onRemoveCategory(@Nonnull FilterWrapper value) {
        mFilter
                .removeCategory(value.getValue());
    }
    //endregion

    // region Payment methods
    public LiveData<Boolean> arePaymentsAvailable() {
        return Transformations.map(mExpenseRepo.getPaymentMethods(),
                new Function<List<String>, Boolean>() {
                    @Override
                    public Boolean apply(List<String> input) {
                        mPayments = input;
                        return input != null && input.size() > 0;
                    }
                });
    }

    public List<FilterWrapper> getPayments() {
        if (mPayments == null || mPayments.size() == 0) {
            return null;
        }
        List<FilterWrapper> wrappers = new ArrayList<>();
        for (String value : mPayments) {
            boolean selected = mFilter.getPaymentMethods() != null &&
                    mFilter.getPaymentMethods().contains(value);
            wrappers.add(new FilterWrapper(value, selected));
        }
        return wrappers;
    }

    public void onAddPaymentMethod(@Nonnull FilterWrapper value) {
        mFilter
                .addPaymentMethod(value.getValue());
    }

    public void onRemovePaymentMethod(@Nonnull FilterWrapper value) {
        mFilter
                .removePaymentMethod(value.getValue());
    }
    //endregion

    // region Income
    public boolean isIncomeAvailable() {
        return SecretMessageHelper.isIncomeEnabled();
    }

    public List<FilterWrapper> getIncomes() {
        List<FilterWrapper> wrappers = new ArrayList<>();
        String value = getString(R.string.filter_options_income_incomes);
        boolean selected = mFilter.getIsIncome() == null || mFilter.getIsIncome();
        wrappers.add(new FilterWrapper(value, selected));

        value = getString(R.string.filter_options_income_expenses);
        selected = mFilter.getIsIncome() == null || !mFilter.getIsIncome();
        wrappers.add(new FilterWrapper(value, selected));
        return wrappers;
    }

    public void onAddIncome(@Nonnull List<FilterWrapper> oldValues, @Nonnull FilterWrapper value) {
        List<FilterWrapper> newValues = onItemSelected(oldValues, value);
        if (newValues == null) {
            return;
        }
        onModifyIncome(newValues);
    }

    public void onRemoveIncome(@Nonnull List<FilterWrapper> oldValues,
            @Nonnull FilterWrapper value) {
        List<FilterWrapper> newValues = onItemUnselected(oldValues, value);
        if (newValues == null) {
            return;
        }
        boolean noneSelected = true;
        for (FilterWrapper wrapper : newValues) {
            if (wrapper.isSelected()) {
                noneSelected = false;
                break;
            }
        }
        if (noneSelected) {
            return;
        }
        onModifyIncome(newValues);
    }

    private void onModifyIncome(@Nonnull List<FilterWrapper> newValues) {
        boolean incomes = false, expenses = false;
        for (FilterWrapper wrapper : newValues) {
            if (wrapper.getValue().equals(getString(R.string.filter_options_income_incomes))) {
                incomes = wrapper.isSelected();
            } else if (wrapper.getValue()
                    .equals(getString(R.string.filter_options_income_expenses))) {
                expenses = wrapper.isSelected();
            }
        }
        if ((incomes && expenses) || (!incomes && !expenses)) { // Both
            mFilter.setIsIncome(null);
        } else if (incomes) { // Incomes
            mFilter.setIsIncome(true);
        } else { // Expenses
            mFilter.setIsIncome(false);
        }
    }
    //endregion

    // region Flag
    public List<FilterWrapper> getFlagStatuses() {
        List<FilterWrapper> wrappers = new ArrayList<>();
        String value = getString(R.string.filter_options_flag_flagged);
        boolean selected = mFilter.getIsStarred() == null || mFilter.getIsStarred();
        wrappers.add(new FilterWrapper(value, selected));

        value = getString(R.string.filter_options_flag_not_flagged);
        selected = mFilter.getIsStarred() == null || !mFilter.getIsStarred();
        wrappers.add(new FilterWrapper(value, selected));
        return wrappers;
    }

    public void onAddFlag(@Nonnull List<FilterWrapper> oldValues, @Nonnull FilterWrapper value) {
        List<FilterWrapper> newValues = onItemSelected(oldValues, value);
        if (newValues == null) {
            return;
        }
        onModifyFlag(newValues);
    }

    public void onRemoveFlag(@Nonnull List<FilterWrapper> oldValues,
            @Nonnull FilterWrapper value) {
        List<FilterWrapper> newValues = onItemUnselected(oldValues, value);
        if (newValues == null) {
            return;
        }
        boolean noneSelected = true;
        for (FilterWrapper wrapper : newValues) {
            if (wrapper.isSelected()) {
                noneSelected = false;
                break;
            }
        }
        if (noneSelected) {
            return;
        }
        onModifyFlag(newValues);
    }

    private void onModifyFlag(@Nonnull List<FilterWrapper> newValues) {
        boolean flagged = false, notFlagged = false;
        for (FilterWrapper wrapper : newValues) {
            if (wrapper.getValue().equals(getString(R.string.filter_options_flag_flagged))) {
                flagged = wrapper.isSelected();
            } else if (wrapper.getValue()
                    .equals(getString(R.string.filter_options_flag_not_flagged))) {
                notFlagged = wrapper.isSelected();
            }
        }
        if ((flagged && notFlagged) || (!flagged && !notFlagged)) { // Both
            mFilter.setIsStarred(null);
        } else if (flagged) { // Flagged
            mFilter.setIsStarred(true);
        } else { // Not flagged
            mFilter.setIsStarred(false);
        }
    }
    //endregion

    @Nullable
    private List<FilterWrapper> onItemSelected(List<FilterWrapper> values, FilterWrapper item) {
        FilterWrapper newValue = new FilterWrapper(item.getValue(), true);
        if (values == null) {
            return null;
        }
        int index = values.indexOf(item);
        if (index == -1) {
            return null;
        }
        values.remove(item);
        values.add(index, newValue);
        return values;
    }

    @Nullable
    private List<FilterWrapper> onItemUnselected(List<FilterWrapper> values, FilterWrapper item) {
        FilterWrapper newValue = new FilterWrapper(item.getValue(), false);
        if (values == null) {
            return null;
        }
        int index = values.indexOf(item);
        if (index == -1) {
            return null;
        }
        values.remove(item);
        values.add(index, newValue);
        return values;
    }

    public String getString(@StringRes int id) {
        return MainApplication.getInstance().getString(id);
    }
}

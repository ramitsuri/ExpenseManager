package com.ramitsuri.expensemanager.viewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.FilterWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.annotation.Nonnull;

public class FilterOptionsViewModel extends ViewModel {

    private final Filter mFilter;
    private final ExpenseRepository mExpenseRepo;

    private List<String> mCategories, mPayments;
    private SortedSet<Integer> mYears;
    private final TimeZone mTimeZone = AppHelper.getTimeZone();

    public FilterOptionsViewModel(@Nonnull Filter filter) {
        super();
        mFilter = filter;

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
    }

    public Filter clear() {
        mFilter.clear()
                .getDefault();
        return mFilter;
    }

    public Filter get() {
        return mFilter;
    }

    // region DateTime
    public LiveData<Boolean> areYearsAvailable() {
        return Transformations.map(mExpenseRepo.getDateTimes(),
                new Function<List<Long>, Boolean>() {
                    @Override
                    public Boolean apply(List<Long> input) {
                        mYears = new TreeSet<>();
                        for (Long value : input) {
                            ZonedDateTime zonedDateTime =
                                    DateHelper.getZonedDateTime(value, mTimeZone.toZoneId());
                            mYears.add(zonedDateTime.getYear());
                        }
                        return input.size() > 0;
                    }
                });
    }

    @Nullable
    public List<FilterWrapper> getYears() {
        SortedSet<Integer> years = mYears;
        if (years == null || years.size() == 0) {
            return null;
        }
        List<FilterWrapper> wrappers = new ArrayList<>();
        Set<Integer> filterYears = mFilter.getYears();
        for (Integer value : years) {
            boolean selected = filterYears != null && filterYears.contains(value);
            wrappers.add(new FilterWrapper(String.valueOf(value), selected));
        }
        return wrappers;
    }

    public void onAddYear(@Nonnull FilterWrapper value) {
        mFilter.addYear(Integer.parseInt(value.getValue()));
    }

    public void onRemoveYear(@Nonnull FilterWrapper value) {
        Set<Integer> years = mFilter.getYears();
        if (years != null && years.size() == 1) {
            return;
        }
        mFilter.removeYear(Integer.parseInt(value.getValue()));
    }

    @NonNull
    public List<FilterWrapper> getMonths() {
        List<FilterWrapper> wrappers = new ArrayList<>();
        Set<Integer> filterMonths = mFilter.getMonths();
        int index = 1;
        for (String month : AppHelper.getMonths()) {
            boolean selected = filterMonths != null &&
                    filterMonths.contains(index);
            wrappers.add(new FilterWrapper(month, selected));
            index = index + 1;
        }
        return wrappers;
    }

    public int getSelectedMonthPosition() {
        List<FilterWrapper> wrappers = getMonths();
        int index = 0;
        for (FilterWrapper wrapper : wrappers) {
            if (wrapper.isSelected()) {
                break;
            }
            index++;
        }
        return index;
    }

    public void onAddMonth(@Nonnull FilterWrapper value) {
        int month = getMonthPicked(value.getValue());
        mFilter.addMonth(month);
    }

    public void onRemoveMonth(@Nonnull FilterWrapper value) {
        Set<Integer> months = mFilter.getMonths();
        if (months != null && months.size() == 1) {
            return;
        }
        int month = getMonthPicked(value.getValue());
        mFilter.removeMonth(month);
    }

    /**
     * Converts picked month into its corresponding index in the range 0 - 11
     */
    private int getMonthPicked(String pickedMonth) {
        int index = 1;
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

    // region Flag
    public List<FilterWrapper> getFlagStatuses() {
        Boolean filterStarred = mFilter.isStarred();
        List<FilterWrapper> wrappers = new ArrayList<>();
        String value = getString(R.string.filter_options_flag_flagged);
        boolean selected = filterStarred == null || filterStarred;
        wrappers.add(new FilterWrapper(value, selected));

        value = getString(R.string.filter_options_flag_not_flagged);
        selected = filterStarred == null || !filterStarred;
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
            mFilter.setStarred(null);
        } else if (flagged) { // Flagged
            mFilter.setStarred(true);
        } else { // Not flagged
            mFilter.setStarred(false);
        }
    }
    //endregion

    // region Record Type
    public List<FilterWrapper> getRecordTypes() {
        List<FilterWrapper> wrappers = new ArrayList<>();
        String value = getString(R.string.filter_options_record_monthly);
        boolean selected = mFilter.getRecordType() == null ||
                mFilter.getRecordType().equals(RecordType.MONTHLY);
        wrappers.add(new FilterWrapper(value, selected));

        value = getString(R.string.filter_options_record_annual);
        selected = mFilter.getRecordType() == null ||
                mFilter.getRecordType().equals(RecordType.ANNUAL);
        wrappers.add(new FilterWrapper(value, selected));
        return wrappers;
    }

    public void onAddRecordType(@Nonnull List<FilterWrapper> oldValues,
            @Nonnull FilterWrapper value) {
        List<FilterWrapper> newValues = onItemSelected(oldValues, value);
        if (newValues == null) {
            return;
        }
        onModifyRecordType(newValues);
    }

    public void onRemoveRecordType(@Nonnull List<FilterWrapper> oldValues,
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
        onModifyRecordType(newValues);
    }

    private void onModifyRecordType(@Nonnull List<FilterWrapper> newValues) {
        boolean monthly = false, annual = false;
        for (FilterWrapper wrapper : newValues) {
            if (wrapper.getValue().equals(getString(R.string.filter_options_record_monthly))) {
                monthly = wrapper.isSelected();
            } else if (wrapper.getValue()
                    .equals(getString(R.string.filter_options_record_annual))) {
                annual = wrapper.isSelected();
            }
        }
        if ((monthly && annual) || (!monthly && !annual)) { // Both
            mFilter.setRecordType(null);
        } else if (monthly) { // Monthly
            mFilter.setRecordType(RecordType.MONTHLY);
        } else { // Annual
            mFilter.setRecordType(RecordType.ANNUAL);
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

package com.ramitsuri.expensemanager.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.Calculator;
import com.ramitsuri.expensemanager.utils.TransformationHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import timber.log.Timber;

public class AllExpensesViewModel extends ViewModel {

    private final ExpenseRepository mRepository;

    private List<Expense> mExpenses;
    private Filter mFilter;
    private MutableLiveData<String> mFilterInfo;
    private final TimeZone mTimeZone = AppHelper.getTimeZone();

    public AllExpensesViewModel() {
        super();
        mRepository = MainApplication.getInstance().getExpenseRepo();
        mFilter = getDefaultFilter();
        mFilterInfo = new MutableLiveData<>();
        updateFilterInfo();
    }

    /*
     * Expenses
     */
    @Nullable
    public LiveData<List<ExpenseWrapper>> getExpenseWrappers() {
        return Transformations.map(mRepository.getExpenses(),
                new Function<List<Expense>, List<ExpenseWrapper>>() {
                    @Override
                    public List<ExpenseWrapper> apply(List<Expense> input) {
                        mExpenses = input;
                        return TransformationHelper.toExpenseWrapperList(input);
                    }
                });
    }

    @Nullable
    public List<Expense> getExpenses() {
        return mExpenses;
    }

    public int getExpensesSize() {
        if (mExpenses == null) {
            return 0;
        } else {
            return mExpenses.size();
        }
    }

    public BigDecimal getExpensesTotal() {
        if (mExpenses == null) {
            return BigDecimal.ZERO;
        }
        Calculator calculator = new Calculator(mExpenses, null, false, false);
        calculator.calculate();
        return calculator.getExpenseTotalValue();
    }

    @Nonnull
    public Filter getFilter() {
        return mFilter;
    }

    public void onExpenseFilterApplied(@Nullable Filter filter) {
        if (filter == null) {
            filter = getDefaultFilter();
        }
        Timber.i("Filtering for %s", filter.toString());
        mFilter = filter;
        mRepository.getForFilter(mFilter);
        updateFilterInfo();
    }

    public LiveData<Expense> duplicateExpense(@Nonnull Expense expense) {
        Expense duplicate = new Expense(expense);
        duplicate.generateIdentifier();
        duplicate.setSynced(false);
        return mRepository.insertAndGet(duplicate, mFilter);
    }

    public void deleteExpense(@Nonnull Expense expense) {
        mRepository.delete(expense, mFilter);
    }

    public LiveData<String> getFilterInfo() {
        return mFilterInfo;
    }

    private void updateFilterInfo() {
        mFilterInfo.postValue(mFilter.toFriendlyString());
    }

    @Nonnull
    private Filter getDefaultFilter() {
        Filter filter = new Filter(mTimeZone).getDefault();
        filter.setRecordType(RecordType.MONTHLY);
        return filter;
    }

    public void clearFilter() {
        Timber.i("Clearing filter");
        mFilter.clear()
                .getDefault();
        mRepository.getForFilter(mFilter);
        updateFilterInfo();
    }
}

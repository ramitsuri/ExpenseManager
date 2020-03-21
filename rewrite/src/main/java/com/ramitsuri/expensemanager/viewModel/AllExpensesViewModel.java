package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.Calculator;
import com.ramitsuri.expensemanager.utils.TransformationHelper;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class AllExpensesViewModel extends ViewModel {

    private ExpenseRepository mRepository;
    private SheetRepository mSheetRepository;

    private List<Expense> mExpenses;
    private int mSelectedSheetId;

    public AllExpensesViewModel() {
        super();
        mRepository = MainApplication.getInstance().getExpenseRepo();
        mSheetRepository = MainApplication.getInstance().getSheetRepository();
        setSelectedSheetId(AppHelper.getDefaultSheetId());
    }

    /*
     * Sheet infos
     */

    public int getSelectedSheetId() {
        return mSelectedSheetId;
    }

    public void setSelectedSheetId(int selectedSheetId) {
        mSelectedSheetId = selectedSheetId;
        mSheetRepository.refreshSheetName(selectedSheetId);
    }

    public LiveData<String> getSheetName() {
        return mSheetRepository.getSheetName();
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

    public void refreshExpenseWrappers() {
        mRepository.getFromSheet(mSelectedSheetId);
    }

    public LiveData<Expense> duplicateExpense(@Nonnull Expense expense) {
        Expense duplicate = new Expense(expense);
        duplicate.setIsSynced(false);
        return mRepository.insertAndGet(duplicate, expense.getSheetId());
    }

    public void deleteExpense(@Nonnull Expense expense) {
        mRepository.delete(expense, expense.getSheetId());
    }
}

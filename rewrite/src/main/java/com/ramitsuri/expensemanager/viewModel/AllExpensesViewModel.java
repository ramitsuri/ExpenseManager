package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.TransformationHelper;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class AllExpensesViewModel extends ViewModel {

    private ExpenseRepository mRepository;

    private SheetInfo mSelectedSheetInfo;
    private List<SheetInfo> mSheetInfos;
    private List<Expense> mExpenses;

    private LiveData<List<SheetInfo>> mSheetInfosLiveData;

    public AllExpensesViewModel() {
        super();
        SheetRepository sheetRepository = MainApplication.getInstance().getSheetRepository();

        mRepository = MainApplication.getInstance().getExpenseRepo();

        mSelectedSheetInfo = new SheetInfo();
        mSelectedSheetInfo.setSheetId(AppHelper.getDefaultSheetId());
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("SpreadsheetId is null or empty");
        } else {
            mSheetInfosLiveData =
                    Transformations.map(sheetRepository.getSheetInfos(spreadsheetId, false),
                            new Function<List<SheetInfo>, List<SheetInfo>>() {
                                @Override
                                public List<SheetInfo> apply(List<SheetInfo> input) {
                                    mSheetInfos = TransformationHelper.filterSheetInfos(input);
                                    return mSheetInfos;
                                }
                            });
        }
    }

    /*
     * Sheet infos
     */
    @Nullable
    public LiveData<List<SheetInfo>> getSheetInfosLiveData() {
        return mSheetInfosLiveData;
    }

    @Nullable
    public List<SheetInfo> getSheetInfos() {
        return mSheetInfos;
    }

    public int getSelectedSheetId() {
        return mSelectedSheetInfo.getSheetId();
    }

    public void setSelectedSheetInfo(SheetInfo selectedSheetInfo) {
        mSelectedSheetInfo = selectedSheetInfo;
    }

    @Nullable
    public String getSelectedSheetName() {
        if (mSelectedSheetInfo != null) {
            return mSelectedSheetInfo.getSheetName();
        }
        return null;
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

    public void getWrappersFromSheet(int sheetId) {
        mRepository.getFromSheet(sheetId);
    }

    public void getWrappersFromSheet() {
        mRepository.getFromSheet(mSelectedSheetInfo);
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

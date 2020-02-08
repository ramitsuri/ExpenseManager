package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.ExpenseSheetsRepository;
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

    private int mSelectedSheetId;
    private LiveData<List<SheetInfo>> mSheetInfosLiveData;
    private List<SheetInfo> mSheetInfos;
    private ExpenseSheetsRepository mRepository;

    public AllExpensesViewModel() {
        super();

        mSelectedSheetId = AppHelper.getDefaultSheetId();
        SheetRepository sheetRepository = MainApplication.getInstance().getSheetRepository();
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

        mRepository = MainApplication.getInstance().getExpenseSheetsRepo();
    }

    @Nullable
    public LiveData<List<SheetInfo>> getSheetInfosLiveData() {
        return mSheetInfosLiveData;
    }

    @Nullable
    public List<SheetInfo> getSheetInfos() {
        return mSheetInfos;
    }

    @Nullable
    public LiveData<List<ExpenseWrapper>> getExpenses(SheetInfo sheetInfo) {
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("SpreadsheetId is null or empty");
            return null;
        }
        return Transformations.map(mRepository.getExpensesFromSheet(spreadsheetId, sheetInfo),
                new Function<List<Expense>, List<ExpenseWrapper>>() {
                    @Override
                    public List<ExpenseWrapper> apply(List<Expense> input) {
                        Timber.i("Transforming");
                        return TransformationHelper.toExpenseWrapperList(input);
                    }
                });
    }

    public LiveData<Expense> duplicateExpense(@Nonnull Expense expense) {
        Expense duplicate = new Expense(expense);
        duplicate.setIsSynced(false);
        return mRepository.insertAndGetExpense(duplicate);
    }

    public int getSelectedSheetId() {
        return mSelectedSheetId;
    }

    public void setSelectedSheetId(int selectedSheetId) {
        mSelectedSheetId = selectedSheetId;
    }

    @Nullable
    public String getSelectedSheetName() {
        if (mSheetInfos != null) {
            for (SheetInfo sheetInfo : mSheetInfos) {
                if (mSelectedSheetId == sheetInfo.getSheetId()) {
                    return sheetInfo.getSheetName();
                }
            }
        }
        return null;
    }
}

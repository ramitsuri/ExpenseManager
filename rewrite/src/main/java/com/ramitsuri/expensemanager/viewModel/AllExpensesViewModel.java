package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.ExpenseSheetsRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.TransformationHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class AllExpensesViewModel extends ViewModel {

    private LiveData<List<SheetInfo>> mSheetInfos;
    private SheetRepository mSheetRepository;
    private ExpenseSheetsRepository mRepository;

    public AllExpensesViewModel() {
        super();

        mSheetRepository = MainApplication.getInstance().getSheetRepository();
        SheetRepository sheetRepo = MainApplication.getInstance().getSheetRepository();
        mSheetInfos = Transformations.map(sheetRepo.getSheetInfos(false),
                new Function<List<SheetInfo>, List<SheetInfo>>() {
                    @Override
                    public List<SheetInfo> apply(List<SheetInfo> input) {
                        return TransformationHelper.filterSheetInfos(input);
                    }
                });

        mRepository = MainApplication.getInstance().getExpenseSheetsRepo();
    }

    public LiveData<List<SheetInfo>> getSheetInfos() {
        return mSheetInfos;
    }

    public LiveData<List<ExpenseWrapper>> getExpenses(SheetInfo sheetInfo) {
        return Transformations.map(mRepository.getExpensesFromSheet(sheetInfo),
                new Function<List<Expense>, List<ExpenseWrapper>>() {
                    @Override
                    public List<ExpenseWrapper> apply(List<Expense> input) {
                        return TransformationHelper.toExpenseWrapperList(input);
                    }
                });
    }
}

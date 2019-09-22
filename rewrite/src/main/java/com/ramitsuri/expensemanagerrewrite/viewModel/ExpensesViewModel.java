package com.ramitsuri.expensemanagerrewrite.viewModel;

import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.ExpenseWrapper;
import com.ramitsuri.expensemanagerrewrite.utils.TransformationHelper;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class ExpensesViewModel extends ViewModel {

    private ExpenseRepository mExpenseRepo;
    private LiveData<List<ExpenseWrapper>> mExpenses;

    public ExpensesViewModel() {
        super();

        MainApplication.getInstance().initRepos();
        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mExpenses = Transformations.map(mExpenseRepo.getExpenses(),
                new Function<List<Expense>, List<ExpenseWrapper>>() {
                    @Override
                    public List<ExpenseWrapper> apply(List<Expense> input) {
                        return TransformationHelper.toExpenseWrapperList(input);
                    }
                });
    }

    public LiveData<List<ExpenseWrapper>> getExpenses() {
        return mExpenses;
    }
}

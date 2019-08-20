package com.ramitsuri.expensemanagerrewrite.viewModel;

import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ExpensesViewModel extends ViewModel {

    private ExpenseRepository mExpenseRepo;
    private LiveData<List<Expense>> mExpenses;

    public ExpensesViewModel() {
        super();

        MainApplication.getInstance().initRepos();
        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mExpenses = mExpenseRepo.getExpenses();
    }

    public LiveData<List<Expense>> getExpenses() {
        return mExpenses;
    }
}

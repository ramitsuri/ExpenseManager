package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.entities.Budget;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupBudgetsViewModel extends ViewModel {

    private MutableLiveData<List<Budget>> mBudgets;

    public SetupBudgetsViewModel() {
        super();

        mBudgets = budgetRepo().getBudgetss();
    }

    public LiveData<List<Budget>> getBudgets() {
        return mBudgets;
    }

    public void setBudgets(List<Budget> budgets) {
        mBudgets.postValue(budgets);
    }

    public void saveBudgets(List<Budget> budgets) {
        if (budgetRepo() != null) {
            budgetRepo().setBudgets(budgets);
        }
    }

    private BudgetRepository budgetRepo() {
        return MainApplication.getInstance().getBudgetRepository();
    }
}

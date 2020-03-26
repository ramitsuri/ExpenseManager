package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Budget;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BudgetRepository {
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public BudgetRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
    }

    public LiveData<List<Budget>> getBudgets() {
        final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Budget> values = mDatabase.budgetDao().getAll();
                budgets.postValue(values);
            }
        });
        return budgets;
    }

    public MutableLiveData<List<Budget>> getBudgetss() {
        final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Budget> values = mDatabase.budgetDao().getAll();
                budgets.postValue(values);
            }
        });
        return budgets;
    }

    public void setBudgets(final List<Budget> budgets) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.budgetDao().setAll(budgets);
            }
        });
    }
}

package com.ramitsuri.expensemanagerrewrite.data.repository;

import com.ramitsuri.expensemanagerrewrite.data.AppExecutors;
import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ExpenseRepository {

    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    private LiveData<List<Expense>> mExpenses;

    public ExpenseRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
        mExpenses = mDatabase.expenseDao().getAll();
    }

    public LiveData<List<Expense>> getExpenses() {
        return mExpenses;
    }

    public LiveData<List<Expense>> getStarredExpenses() {
        final MutableLiveData<List<Expense>> expenses = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                expenses.postValue(mDatabase.expenseDao().getAllStarred());
            }
        });
        return expenses;
    }

    public LiveData<List<Expense>> getUnsyncedExpenses() {
        final MutableLiveData<List<Expense>> expenses = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                expenses.postValue(mDatabase.expenseDao().getAllUnsynced());
            }
        });
        return expenses;
    }

    public void insertExpense(final Expense expense) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().insert(expense);
            }
        });
    }
}

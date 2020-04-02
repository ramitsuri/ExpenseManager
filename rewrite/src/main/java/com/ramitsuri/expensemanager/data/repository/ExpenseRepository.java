package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ExpenseRepository {
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;
    private MutableLiveData<List<Expense>> mExpenses;

    public ExpenseRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
        mExpenses = new MutableLiveData<>();
    }

    public LiveData<List<Expense>> getExpenses() {
        return mExpenses;
    }

    public void getFromSheet(final int sheetId) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Expense> values = mDatabase.expenseDao().getAllForSheet(sheetId);
                mExpenses.postValue(values);
            }
        });
    }

    public void getForFilter(@Nonnull Filter filter) {
        final long fromDateTime = filter.getFromDateTime();
        final long toDateTime = filter.getToDateTime();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Expense> values =
                        mDatabase.expenseDao().getAllForDateRange(fromDateTime, toDateTime);
                mExpenses.postValue(values);
            }
        });
    }

    public void insert(final Expense expense) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().insert(expense);
            }
        });
    }

    public void edit(final Expense expense) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().updateExpense(expense);
            }
        });
    }

    public void delete(final Expense expense,
            @Nonnull Filter filter) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().deleteExpense(expense.getId());
            }
        });

        // Refresh expenses as they don't refresh automatically
        getForFilter(filter);
    }

    public void delete() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().deleteAll();
            }
        });
    }

    public LiveData<Expense> insertAndGet(@Nonnull final Expense expense,
            @Nonnull Filter filter) {
        final MutableLiveData<Expense> duplicate = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Expense value = mDatabase.expenseDao().insertAndGetExpense(expense);
                duplicate.postValue(value);
            }
        });

        // Refresh expenses as they don't refresh automatically
        getForFilter(filter);
        return duplicate;
    }
}

package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;

public class BaseRepository {
    protected final AppExecutors mExecutors;
    protected final ExpenseManagerDatabase mDatabase;

    public BaseRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
    }
}

package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LogRepository {

    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;
    private MutableLiveData<List<Log>> mLogs;

    public LogRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
        mLogs = new MutableLiveData<>();
    }

    public void insertLog(final Log log) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.logDao().insert(log);
            }
        });
    }

    public LiveData<List<Log>> getUnacknowledgedLogs() {
        final MutableLiveData<List<Log>> logs = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Log> values = mDatabase.logDao().getUnacknowledged();
                logs.postValue(values);
            }
        });
        return logs;
    }

    public LiveData<List<Log>> getLogs() {
        return mLogs;
    }

    public void getAllLogs() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Log> values = mDatabase.logDao().getAll();
                mLogs.postValue(values);
            }
        });
    }

    public void deleteAcknowledged() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.logDao().deleteAcknowledged();
            }
        });
    }

    public void deleteAll() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.logDao().deleteAll();
            }
        });
    }
}

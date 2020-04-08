package com.ramitsuri.expensemanager.data.repository;

import android.database.sqlite.SQLiteConstraintException;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.EditedSheet;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class EditedSheetRepository {

    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;
    private MutableLiveData<List<Integer>> mEditedMonths;

    public EditedSheetRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
        mEditedMonths = new MutableLiveData<>();
    }

    public void insertEditedSheet(final EditedSheet editedSheet) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDatabase.editedSheetDao().insert(editedSheet);
                } catch (SQLiteConstraintException e) {
                    Timber.i("Sheet id already exists, not inserted");
                }
            }
        });
    }

    public LiveData<List<Integer>> getEditedMonths() {
        return mEditedMonths;
    }

    public void refreshEditedMonths() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Integer> values = mDatabase.editedSheetDao().getAll();
                mEditedMonths.postValue(values);
            }
        });
    }

    public void deleteAll() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.editedSheetDao().deleteAll();
            }
        });
    }
}

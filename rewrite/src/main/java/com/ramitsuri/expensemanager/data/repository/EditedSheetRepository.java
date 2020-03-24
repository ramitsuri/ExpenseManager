package com.ramitsuri.expensemanager.data.repository;

import android.database.sqlite.SQLiteConstraintException;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.EditedSheet;
import com.ramitsuri.expensemanager.entities.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class EditedSheetRepository {

    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public EditedSheetRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        mExecutors = executors;
        mDatabase = database;
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

    public LiveData<List<Integer>> getAllEditedSheets() {
        final MutableLiveData<List<Integer>> editedSheetsIds = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Integer> values = mDatabase.editedSheetDao().getAll();
                editedSheetsIds.postValue(values);
            }
        });
        return editedSheetsIds;
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

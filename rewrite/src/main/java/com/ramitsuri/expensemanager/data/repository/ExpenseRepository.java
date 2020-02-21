package com.ramitsuri.expensemanager.data.repository;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

import static com.ramitsuri.expensemanager.Constants.Sheets.EXPENSE_RANGE;

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

    public void getFromSheet(@Nonnull final SheetInfo sheetInfo) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Timber.i("Fetching expenses from Spreadsheet %s", sheetInfo);
                String spreadsheetId = AppHelper.getSpreadsheetId();
                if (!TextUtils.isEmpty(spreadsheetId)) {
                    List<Expense> values = new ArrayList<>();
                    String range = sheetInfo.getSheetName() + EXPENSE_RANGE;
                    // Get from spreadsheet
                    RangeConsumerResponse response =
                            MainApplication.getInstance().getSheetRepository()
                                    .getRangeDataResponse(spreadsheetId, range);
                    if (response.getObjectLists() != null) {
                        for (List<Object> objects : response.getObjectLists()) {
                            if (objects == null || objects.size() < 5) {
                                continue;
                            }
                            Expense expense = new Expense(objects, sheetInfo.getSheetId());
                            values.add(expense);
                        }
                    }

                    // Save to db
                    Timber.i("Deleting already backed up expenses for %s and saving new ones",
                            sheetInfo);
                    mDatabase.expenseDao().insert(values, sheetInfo.getSheetId());

                    // Refresh expenses as they don't refresh automatically
                    if (sheetInfo.getSheetId() != Constants.Basic.UNDEFINED) {
                        getFromSheet(sheetInfo.getSheetId());
                    }
                } else {
                    Timber.i("SpreadsheetId is null or empty");
                }
            }
        });
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
            final int sheetId) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().deleteExpense(expense.getId());
            }
        });

        // Refresh expenses as they don't refresh automatically
        if (sheetId != Constants.Basic.UNDEFINED) {
            getFromSheet(sheetId);
        }
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
            final int sheetId) {
        final MutableLiveData<Expense> duplicate = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Expense value = mDatabase.expenseDao().insertAndGetExpense(expense);
                duplicate.postValue(value);
            }
        });

        // Refresh expenses as they don't refresh automatically
        if (sheetId != Constants.Basic.UNDEFINED) {
            getFromSheet(sheetId);
        }
        return duplicate;
    }
}

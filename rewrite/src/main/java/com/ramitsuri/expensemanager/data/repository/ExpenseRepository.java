package com.ramitsuri.expensemanager.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.backup.BackupCallback;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.work.WorkResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Nonnull;

public class ExpenseRepository extends BaseRepository {
    private final MutableLiveData<List<Expense>> mExpenses;
    private final MutableLiveData<List<String>> mStores;

    public ExpenseRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        super(executors, database);
        mExpenses = new MutableLiveData<>();
        mStores = new MutableLiveData<>();
    }

    public LiveData<List<Expense>> getExpenses() {
        return mExpenses;
    }

    public void getForFilter(@Nonnull final Filter filter) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Expense> values = mDatabase.expenseDao().getForFilter(filter);
                mExpenses.postValue(values);
            }
        });
    }

    public LiveData<List<String>> getStores() {
        return mStores;
    }

    public LiveData<List<String>> getCategories() {
        final MutableLiveData<List<String>> categories = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> values = mDatabase.expenseDao().getCategories();
                categories.postValue(values);
            }
        });
        return categories;
    }

    public LiveData<List<String>> getPaymentMethods() {
        final MutableLiveData<List<String>> paymentMethods = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> values = mDatabase.expenseDao().getPaymentMethods();
                paymentMethods.postValue(values);
            }
        });
        return paymentMethods;
    }

    public void refreshStores(final String startsWith) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> values = mDatabase.expenseDao().getStores(startsWith);
                mStores.postValue(values);
            }
        });
    }

    public LiveData<Expense> getForStore(@Nonnull final String store) {
        final MutableLiveData<Expense> expense = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Expense value = mDatabase.expenseDao().getForStore(store);
                expense.postValue(value);
            }
        });
        return expense;
    }

    @NonNull
    public LiveData<List<Long>> getDateTimes() {
        final MutableLiveData<List<Long>> dateTimes = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Long> values = mDatabase.expenseDao().getDateTimes();
                dateTimes.postValue(values);
            }
        });
        return dateTimes;
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

    public void updateSetIdentifier() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().updateSetIdentifier();
            }
        });
    }

    public void insert(@Nonnull final List<Expense> expenses, Filter filter) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.expenseDao().insert(expenses);
            }
        });

        // Refresh expenses as they don't refresh automatically
        getForFilter(filter);
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

    public void exportAllData(@NonNull OutputStream outputStream,
            final @NonNull BackupCallback callback) {
        mExecutors.diskIO().execute(() -> {
            WorkResult<String> res =
                    MainApplication.getInstance().getInjector().provideExpenseBackupService()
                            .process();
            if (res instanceof WorkResult.Success) {
                String data = ((WorkResult.Success<String>)res).getData();
                try {
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    callback.onSuccess();
                } catch (IOException e) {
                    callback.onFailed(e.getMessage());
                }
            } else {
                callback.onFailed("Backup Service failed");
            }
        });
    }
}

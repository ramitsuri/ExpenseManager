package com.ramitsuri.expensemanager.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class CategoryRepository extends BaseRepository {

    public CategoryRepository(AppExecutors executors, ExpenseManagerDatabase database) {
        super(executors, database);
    }

    public MutableLiveData<List<Category>> getCategories() {
        final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Category> values = mDatabase.categoryDao().getAll();
                categories.postValue(values);
            }
        });
        return categories;
    }

    @Nonnull
    public MutableLiveData<List<String>> getCategoryStrings(@RecordType final String recordType) {
        final MutableLiveData<List<String>> categories = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Category> values = mDatabase.categoryDao().getAll(recordType);
                Timber.i("Categories %s", values);
                List<String> stringValues = new ArrayList<>();
                for (Category value : values) {
                    stringValues.add(value.getName());
                }
                categories.postValue(stringValues);
            }
        });
        return categories;
    }

    public void setCategories(final List<Category> categories) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.categoryDao().setAll(categories);
            }
        });
    }
}

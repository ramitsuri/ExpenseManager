package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.IntDefs.SourceType;
import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CategoryRepository {

    @SourceType
    private int mSourceType;
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public CategoryRepository(AppExecutors executors, ExpenseManagerDatabase database,
            @SourceType int sourceType) {
        mExecutors = executors;
        mDatabase = database;
        mSourceType = SourceType.DB;
    }

    public LiveData<List<Category>> getCategories() {
        final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Category> values = null;
                if (mSourceType == SourceType.LOCAL) {
                    values = DummyData.getAllCategories();
                } else if (mSourceType == SourceType.DB) {
                    values = mDatabase.categoryDao().getAll();
                }
                categories.postValue(values);
            }
        });
        return categories;
    }

    public void setCategories(final List<String> categories) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Category> categoryList = new ArrayList<>();
                for (String categoryName : categories) {
                    Category category = new Category();
                    category.setName(categoryName);
                    categoryList.add(category);
                }
                mDatabase.categoryDao().setAll(categoryList);
            }
        });
    }
}

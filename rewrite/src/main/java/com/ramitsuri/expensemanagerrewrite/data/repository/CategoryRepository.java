package com.ramitsuri.expensemanagerrewrite.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.data.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.entities.Category;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

public class CategoryRepository {

    private CategoryDao mDao;
    private LiveData<List<Category>> mCategories;

    public CategoryRepository(Application application) {
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance(application);
        mDao = database.categoryDao();
        mCategories = mDao.getAll();
    }

    public LiveData<List<Category>> getCategories() {
        return mCategories;
    }

    public void setCategories(String[] categories) {
        new insertAsyncTask(mDao).execute(categories);
    }

    private static class insertAsyncTask extends AsyncTask<String[], Void, Void> {

        private CategoryDao mAsyncTaskDao;

        insertAsyncTask(CategoryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String[]... args) {
            List<Category> categories = new ArrayList<>();
            for (String categoryName : args[0]) {
                Category category = new Category();
                category.setName(categoryName);
                categories.add(category);
            }
            mAsyncTaskDao.setAll(categories);
            return null;
        }
    }
}

package com.ramitsuri.expensemanagerrewrite.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.data.dao.ExpenseDao;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ExpenseRepository {
    private ExpenseDao mDao;
    private LiveData<List<Expense>> mExpenses;
    private LiveData<List<Expense>> mAllStarred;
    private LiveData<List<Expense>> mAllUnsynced;

    public ExpenseRepository(Application application) {
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance(application);
        mDao = database.expenseDao();
        mExpenses = mDao.getAll();
    }

    public LiveData<List<Expense>> getExpenses() {
        return mExpenses;
    }

    /*public List<Expense> getStarredExpenses() {
        return mExpenses;
    }

    public List<Expense> getUnsyncedExpenses() {
        return new getUnsyncedTask(mDao).execute();
    }*/

    public void insertExpense(Expense expense){

    }

    /*private static class getStarredTask extends AsyncTask<Void, Void, List<Expense>> {

        private ExpenseDao mAsyncTaskDao;

        public getStarredTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAllStarred();
        }
    }*/

    /*private static class getUnsyncedTask extends AsyncTask<Void, Void, List<Expense>> {

        private ExpenseDao mAsyncTaskDao;

        public getUnsyncedTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAllUnsynced();
        }
    }*/
}

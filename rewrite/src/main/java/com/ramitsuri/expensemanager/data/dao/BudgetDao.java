package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.entities.Budget;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class BudgetDao {

    @Query("SELECT * FROM budget")
    public abstract List<Budget> getAll();

    @Transaction
    public void setAll(List<Budget> budgets) {
        deleteAll();
        insertAll(budgets);
    }

    @Insert
    public abstract void insertAll(List<Budget> budgets);

    @Query("DELETE FROM budget")
    public abstract void deleteAll();
}

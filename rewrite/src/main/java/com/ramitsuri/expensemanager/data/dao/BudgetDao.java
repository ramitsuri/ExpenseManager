package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.data.converter.ListConverter;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

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

    @Transaction
    public void updateCategory(String oldCategory, String newCategory) {
        List<Budget> budgets = getAll();
        for (Budget budget : budgets) {
            List<String> categories = budget.getCategories();
            if (ObjectHelper.contains(categories, oldCategory)) {
                int index = categories.indexOf(oldCategory);
                categories.remove(index);
                categories.add(index, newCategory);
                String newCategories = ListConverter.toString(categories);
                updateCategories(newCategories, budget.getId());
                break;
            }
        }
    }

    @Transaction
    public void deleteCategory(String oldCategory) {
        List<Budget> budgets = getAll();
        for (Budget budget : budgets) {
            List<String> categories = budget.getCategories();
            if (ObjectHelper.contains(categories, oldCategory)) {
                categories.remove(oldCategory);
                String newCategories = ListConverter.toString(categories);
                updateCategories(newCategories, budget.getId());
                break;
            }
        }
    }

    @Query("UPDATE budget SET categories =:categories WHERE mId = :id")
    abstract void updateCategories(String categories, int id);

    @Query("DELETE FROM budget")
    public abstract void deleteAll();
}

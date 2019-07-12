package com.ramitsuri.expensemanagerrewrite.db;

import com.ramitsuri.expensemanagerrewrite.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.entities.Category;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class}, version = 1)
public abstract class ExpenseManagerDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();
}

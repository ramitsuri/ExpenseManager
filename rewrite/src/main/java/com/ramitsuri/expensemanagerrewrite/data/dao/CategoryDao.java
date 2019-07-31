package com.ramitsuri.expensemanagerrewrite.data.dao;

import com.ramitsuri.expensemanagerrewrite.entities.Category;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class CategoryDao {

    @Query("SELECT * FROM category")
    public abstract LiveData<List<Category>> getAll();

    @Transaction
    public void setAll(List<Category> categories) {
        deleteAll();
        insertAll(categories);
    }

    @Insert
    public abstract void insertAll(List<Category> categories);

    @Query("DELETE FROM category")
    public abstract void deleteAll();
}

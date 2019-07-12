package com.ramitsuri.expensemanagerrewrite.dao;

import com.ramitsuri.expensemanagerrewrite.entities.Category;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<Category> getAll();

    @Insert
    void insertAll(Category... categories);

    @Delete
    void deleteAll();
}

package com.ramitsuri.expensemanagerrewrite.data.dao;

import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense")
    LiveData<List<Expense>> getAll();

    @Query("SELECT * FROM expense WHERE is_starred = 1")
    List<Expense> getAllStarred();

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    List<Expense> getAllUnsynced();

    @Insert
    void insert(Expense expense);

    @Query("UPDATE expense SET is_synced = 1 WHERE is_synced = 0")
    void updateUnsynced();

    @Query("UPDATE expense SET is_starred = 1 WHERE mId = :id")
    void setStarred(int id);

    @Query("UPDATE expense SET is_starred = 0 WHERE mId = :id")
    void setUnstarred(int id);

    @Query("DELETE FROM expense")
    void deleteAll();

    @Query("DELETE FROM expense WHERE is_synced = 1")
    void deleteSynced();
}

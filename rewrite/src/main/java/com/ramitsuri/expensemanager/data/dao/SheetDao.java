package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.entities.SheetInfo;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class SheetDao {

    @Query("SELECT * FROM sheetinfo")
    public abstract List<SheetInfo> getAll();

    @Transaction
    public void setAll(List<SheetInfo> sheetInfos) {
        deleteAll();
        insertAll(sheetInfos);
    }

    @Insert
    public abstract void insertAll(List<SheetInfo> sheetInfos);

    @Query("DELETE FROM sheetinfo")
    public abstract void deleteAll();
}

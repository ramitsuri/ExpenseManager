package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.entities.Log;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class LogDao {

    @Query("SELECT * FROM log")
    public abstract List<Log> getUnacknowledged();

    @Insert
    public abstract void insert(Log log);

    @Query("DELETE FROM log where acknowledged = 1")
    public abstract void deleteAcknowledged();
}

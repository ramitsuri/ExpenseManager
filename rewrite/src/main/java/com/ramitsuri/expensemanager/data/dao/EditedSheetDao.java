package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.entities.EditedSheet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class EditedSheetDao {
    /*
     * SELECT
     */
    @Query("SELECT sheet_id FROM EditedSheet")
    public abstract List<Integer> getAll();

    /*
     * INSERT
     */
    @Insert
    public abstract long insert(EditedSheet editedSheet);

    /*
     * DELETE
     */
    @Query("DELETE FROM editedsheet")
    public abstract void deleteAll();
}

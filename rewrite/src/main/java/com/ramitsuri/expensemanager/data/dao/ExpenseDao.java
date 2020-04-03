package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.entities.Expense;

import java.math.BigDecimal;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class ExpenseDao {
    /*
     * SELECT
     */
    @Query("SELECT * FROM expense ORDER BY date_time DESC")
    public abstract LiveData<List<Expense>> getAll();

    @Query("SELECT * FROM expense WHERE sheet_id = :sheetId ORDER BY date_time DESC")
    public abstract List<Expense> getAllForSheet(int sheetId);

    @Query("SELECT * FROM expense WHERE date_time BETWEEN :fromDateTime AND :toDateTime ORDER BY date_time DESC")
    public abstract List<Expense> getAllForDateRange(long fromDateTime, long toDateTime);

    @Query("SELECT * FROM expense WHERE is_starred = 1")
    public abstract List<Expense> getAllStarred();

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    public abstract List<Expense> getAllUnsynced();

    @Query("SELECT * FROM expense WHERE is_synced = 0 OR sheet_id IN (:sheetIds)")
    public abstract List<Expense> getAllForBackup(List<Integer> sheetIds);

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    public abstract LiveData<List<Expense>> getAllUnsyncedLiveData();

    @Query("SELECT * FROM expense WHERE mId = :id")
    public abstract Expense getExpense(long id);

    /*
     * INSERT
     */
    @Insert
    public abstract long insert(Expense expense);

    @Insert
    public abstract void insert(List<Expense> expenses);

    /*
     * UPDATE
     */
    @Query("UPDATE expense SET date_time =:dateTime WHERE mId = :id")
    abstract void updateDateTime(int id, long dateTime);

    @Query("UPDATE expense SET amount =:amount WHERE mId = :id")
    abstract void updateAmount(int id, BigDecimal amount);

    @Query("UPDATE expense SET payment_method =:paymentMethod WHERE mId = :id")
    abstract void updatePaymentMethod(int id, String paymentMethod);

    @Query("UPDATE expense SET category =:category WHERE mId = :id")
    abstract void updateCategory(int id, String category);

    @Query("UPDATE expense SET description =:description WHERE mId = :id")
    abstract void updateDescription(int id, String description);

    @Query("UPDATE expense SET store =:store WHERE mId = :id")
    abstract void updateStore(int id, String store);

    @Query("UPDATE expense SET is_starred =:isStarred WHERE mId = :id")
    abstract void updateIsStarred(int id, boolean isStarred);

    @Query("UPDATE expense SET is_synced =:isSynced WHERE mId = :id")
    abstract void updateIsSynced(int id, boolean isSynced);

    @Query("UPDATE expense SET is_synced = 1 WHERE is_synced = 0")
    public abstract void updateUnsynced();

    @Query("UPDATE expense SET is_starred = 1 WHERE mId = :id")
    public abstract void setStarred(int id);

    @Query("UPDATE expense SET is_starred = 0 WHERE mId = :id")
    public abstract void setUnstarred(int id);

    /*
     * DELETE
     */
    @Query("DELETE FROM expense where mId = :id")
    public abstract void deleteExpense(int id);

    @Query("DELETE FROM expense")
    public abstract void deleteAll();

    @Query("DELETE FROM expense WHERE is_synced = 1")
    public abstract void deleteSynced();

    /*
     * TRANSACTION
     */
    @Transaction
    public Expense insertAndGetExpense(Expense expense) {
        long id = insert(expense);
        return getExpense(id);
    }

    @Transaction
    public void updateExpense(Expense expense) {
        updateDateTime(expense.getId(), expense.getDateTime());
        updateAmount(expense.getId(), expense.getAmount());
        updatePaymentMethod(expense.getId(), expense.getPaymentMethod());
        updateCategory(expense.getId(), expense.getCategory());
        updateDescription(expense.getId(), expense.getDescription());
        updateStore(expense.getId(), expense.getStore());
        updateIsStarred(expense.getId(), expense.isStarred());
        updateIsSynced(expense.getId(), expense.isSynced());
    }
}

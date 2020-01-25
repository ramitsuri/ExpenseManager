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
    @Query("SELECT * FROM expense ORDER BY date_time DESC")
    public abstract LiveData<List<Expense>> getAll();

    @Query("SELECT * FROM expense WHERE is_starred = 1")
    public abstract List<Expense> getAllStarred();

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    public abstract List<Expense> getAllUnsynced();

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    public abstract LiveData<List<Expense>> getAllUnsyncedLiveData();

    @Insert
    public abstract void insert(Expense expense);

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

    @Query("UPDATE expense SET sheet_id = :sheetId WHERE mId = :id")
    public abstract void updateSheetId(int id, int sheetId);

    @Query("UPDATE expense SET is_starred =:isStarred WHERE mId = :id")
    abstract void updateIsStarred(int id, boolean isStarred);

    @Query("UPDATE expense SET is_synced =:isSynced WHERE mId = :id")
    abstract void updateIsSynced(int id, boolean isSynced);

    @Transaction
    public void updateExpense(Expense expense) {
        updateDateTime(expense.getId(), expense.getDateTime());
        updateAmount(expense.getId(), expense.getAmount());
        updatePaymentMethod(expense.getId(), expense.getPaymentMethod());
        updateCategory(expense.getId(), expense.getCategory());
        updateDescription(expense.getId(), expense.getDescription());
        updateStore(expense.getId(), expense.getStore());
        updateSheetId(expense.getId(), expense.getSheetId());
        updateIsStarred(expense.getId(), expense.isStarred());
        updateIsSynced(expense.getId(), expense.isSynced());
    }

    @Query("UPDATE expense SET is_synced = 1 WHERE is_synced = 0")
    public abstract void updateUnsynced();

    @Query("UPDATE expense SET is_starred = 1 WHERE mId = :id")
    public abstract void setStarred(int id);

    @Query("UPDATE expense SET is_starred = 0 WHERE mId = :id")
    public abstract void setUnstarred(int id);

    @Query("DELETE FROM expense where mId = :id")
    public abstract void deleteExpense(int id);

    @Query("DELETE FROM expense")
    public abstract void deleteAll();

    @Query("DELETE FROM expense WHERE is_synced = 1")
    public abstract void deleteSynced();
}

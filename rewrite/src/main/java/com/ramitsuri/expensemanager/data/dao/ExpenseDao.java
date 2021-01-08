package com.ramitsuri.expensemanager.data.dao;

import com.ramitsuri.expensemanager.constants.intDefs.AddType;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

@Dao
public abstract class ExpenseDao {
    /*
     * SELECT
     */
    @Query("SELECT * FROM expense WHERE is_income = 0 ORDER BY date_time DESC")
    public abstract List<Expense> getExpenses();

    @Query("SELECT * FROM expense WHERE date_time BETWEEN :fromDateTime AND :toDateTime AND is_income = 0 ORDER BY date_time DESC")
    public abstract List<Expense> getExpensesForDateRange(long fromDateTime, long toDateTime);

    @Query("SELECT * FROM expense WHERE is_starred = 1")
    public abstract List<Expense> getAllStarred();

    @Query("SELECT * FROM expense WHERE is_synced = 0")
    public abstract List<Expense> getAllUnsynced();

    @RawQuery
    public abstract List<Expense> getForQuery(SupportSQLiteQuery query);

    @Query("SELECT * FROM expense WHERE mId = :id")
    public abstract Expense getExpense(long id);


    @Query("SELECT * FROM expense WHERE identifier = :identifier")
    public abstract Expense getExpense(String identifier);

    @Query("SELECT * FROM expense WHERE is_income = 1 ORDER BY date_time DESC")
    public abstract List<Expense> getIncomes();

    @Query("SELECT DISTINCT store FROM expense WHERE store LIKE :startsWith || '%' ORDER BY date_time DESC")
    public abstract List<String> getStores(String startsWith);

    @Query("SELECT DISTINCT category FROM expense WHERE is_income = 0")
    public abstract List<String> getCategories();

    @Query("SELECT DISTINCT payment_method FROM expense WHERE is_income = 0")
    public abstract List<String> getPaymentMethods();

    @Query("SELECT * FROM expense WHERE store LIKE :store ORDER BY date_time DESC LIMIT 1")
    public abstract Expense getForStore(String store);

    @Query("SELECT * FROM expense WHERE identifier IS NULL OR identifier  = ''")
    public abstract List<Expense> getForEmptyIdentifier();

    @Query("SELECT * FROM expense WHERE record_type = :recordType")
    public abstract List<Expense> getForRecordType(@RecordType String recordType);

    @Query("SELECT DISTINCT date_time FROM expense")
    public abstract List<Long> getDateTimes();

    /*
     * INSERT
     */
    @Insert
    public abstract long insert(@Nonnull Expense expense);

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

    @Query("UPDATE expense SET is_income =:isIncome WHERE mId = :id")
    abstract void updateIsIncome(int id, boolean isIncome);

    @Query("UPDATE expense SET record_type =:recordType WHERE mId = :id")
    abstract void updateRecordType(int id, @RecordType String recordType);

    @Query("UPDATE expense SET identifier = :identifier WHERE mId = :id")
    abstract void updateIdentifier(int id, String identifier);

    @Query("UPDATE expense SET add_type =:addType WHERE mId = :id")
    abstract void updateAddType(int id, @AddType String addType);

    @Query("UPDATE expense SET is_synced = 1 WHERE is_synced = 0")
    public abstract void updateUnsynced();

    @Query("UPDATE expense SET is_synced = 0")
    public abstract void updateSetAllUnsynced();

    @Query("UPDATE expense SET is_starred = 1 WHERE mId = :id")
    public abstract void setStarred(int id);

    @Query("UPDATE expense SET is_starred = 0 WHERE mId = :id")
    public abstract void setUnstarred(int id);

    @RawQuery
    public abstract boolean updateSetUnsyncedForQuery(SupportSQLiteQuery query);

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

    /**
     * @deprecated This method will be removed once backup service is migrated to a non Google Sheet
     * service.
     */
    @Deprecated
    @Transaction
    public List<Expense> getAllForBackup(@Nonnull List<Integer> months, TimeZone timeZone) {
        Filter filter = new Filter(timeZone);
        for (Integer index : months) {
            filter.addMonth(index);
        }
        // SELECT * FROM expense WHERE is_synced = 0 OR (date_time BETWEEN ? AND ?) OR
        // (date_time BETWEEN ? AND ?) OR (date_time BETWEEN ? AND ?)
        SimpleSQLiteQuery query = filter.toQuery();
        return getForQuery(query);
    }

    @Transaction
    public List<Expense> getForFilter(@Nonnull Filter filter) {
        SimpleSQLiteQuery query = filter.toQuery();
        return getForQuery(query);
    }

    // Updates expense
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
        updateIsIncome(expense.getId(), expense.isIncome());
        updateRecordType(expense.getId(), expense.getRecordType());
        updateAddType(expense.getId(), expense.getAddType());
        // Not updating ID and Identifier intentionally
    }

    /** Updates expenses and sets unsynced for month index
     * @deprecated This method will be removed once backup service is migrated to a non Google Sheet
     * service.
     */
    @Deprecated
    @Transaction
    public void updateSetUnsynced(int monthIndex, @NonNull TimeZone timeZone) {
        Filter filter = new Filter(timeZone);
        filter.addMonth(monthIndex);
        updateSetUnsyncedForQuery(filter.toUpdateSyncedQuery());
    }

    @Transaction
    public void updateSetIdentifier() {
        List<Expense> expenses = getForEmptyIdentifier();
        for (Expense expense : expenses) {
            expense.generateIdentifier();
            updateIdentifier(expense.getId(), expense.getIdentifier());
        }
    }
}

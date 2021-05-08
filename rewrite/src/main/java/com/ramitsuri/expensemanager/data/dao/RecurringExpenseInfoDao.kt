package com.ramitsuri.expensemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ramitsuri.expensemanager.constants.intDefs.RecordType
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo

@Dao
abstract class RecurringExpenseInfoDao {
    /*
    * CREATE
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(info: RecurringExpenseInfo): Long

    /*
     * READ
     */
    @Query("SELECT * FROM recurringexpenseinfo ORDER BY last_occur ASC")
    abstract fun readReactive(): LiveData<List<RecurringExpenseInfo>>

    @Query("SELECT * FROM recurringexpenseinfo ORDER BY last_occur ASC")
    abstract fun getAll(): List<RecurringExpenseInfo>

    @Query("SELECT * FROM recurringexpenseinfo WHERE last_occur <= :before ORDER BY last_occur ASC")
    abstract fun readReactive(before: Long): LiveData<List<RecurringExpenseInfo>>

    @Query("SELECT * FROM recurringexpenseinfo WHERE last_occur <= :before ORDER BY last_occur ASC")
    abstract fun read(before: Long): List<RecurringExpenseInfo>

    @Query("SELECT * FROM recurringexpenseinfo WHERE identifier = :identifier LIMIT 1")
    abstract fun read(identifier: String): RecurringExpenseInfo?

    @Query("SELECT * FROM recurringexpenseinfo WHERE identifier = :identifier LIMIT 1")
    abstract fun readReactive(identifier: String): LiveData<RecurringExpenseInfo?>

    /*
     * UPDATE
     */
    @Query("UPDATE recurringexpenseinfo SET last_occur = :recurOn WHERE id = :id")
    abstract fun update(id: Int, recurOn: Long)

    @Query("UPDATE recurringexpenseinfo SET recur_type = :recurType WHERE id = :id")
    abstract fun update(id: Int, @RecordType recurType: String)

    /*
     * DELETE
     */
    @Query("DELETE FROM recurringexpenseinfo WHERE id=:id")
    abstract fun delete(id: Int)

    @Query("DELETE FROM recurringexpenseinfo WHERE identifier=:identifier")
    abstract fun delete(identifier: String)

    @Query("DELETE FROM recurringexpenseinfo")
    abstract fun delete()

    /*
     * TRANSACTIONS
     */
    fun update(recurringExpenses: List<RecurringExpenseInfo>) {
        for (recurringExpense in recurringExpenses) {
            update(recurringExpense.id, recurringExpense.lastOccur)
        }
    }

    /*
     * TRANSACTIONS
     */
    fun insertUpdateOrDelete(recurringExpense: RecurringExpenseInfo) {
        val existing: RecurringExpenseInfo? = read(recurringExpense.identifier)
        if (existing == null) {
            if (recurringExpense.recurType != RecurType.NONE) {
                insert(recurringExpense)
            }
        } else {
            if (recurringExpense.recurType == RecurType.NONE) {
                delete(recurringExpense.identifier)
            } else {
                update(existing.id, recurringExpense.lastOccur)
                update(existing.id, recurringExpense.recurType)
            }
        }
    }
}
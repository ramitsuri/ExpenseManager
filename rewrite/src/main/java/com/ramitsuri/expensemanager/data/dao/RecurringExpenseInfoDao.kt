package com.ramitsuri.expensemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo

@Dao
interface RecurringExpenseInfoDao {
    /*
    * CREATE
    */
    @Insert
    fun insert(info: RecurringExpenseInfo): Long

    /*
     * READ
     */
    @Query("SELECT * FROM recurringexpenseinfo ORDER BY last_occur ASC")
    fun read(): LiveData<List<RecurringExpenseInfo>>

    @Query("SELECT * FROM recurringexpenseinfo WHERE last_occur <= :before ORDER BY last_occur ASC")
    fun read(before: Long): LiveData<List<RecurringExpenseInfo>>

    /*
     * UPDATE
     */
    @Query("UPDATE recurringexpenseinfo SET last_occur = :recurOn WHERE id = :id")
    fun update(id: Int, recurOn: Long)

    /*
     * DELETE
     */
    @Query("DELETE FROM recurringexpenseinfo WHERE id=:id")
    fun delete(id: Int)

    @Query("DELETE FROM recurringexpenseinfo WHERE identifier=:identifier")
    fun delete(identifier: String)

    @Query("DELETE FROM recurringexpenseinfo")
    fun delete()

}
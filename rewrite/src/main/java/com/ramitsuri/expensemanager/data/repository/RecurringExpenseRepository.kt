package com.ramitsuri.expensemanager.data.repository

import androidx.lifecycle.LiveData
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecurringExpenseRepository(private val database: ExpenseManagerDatabase) {
    suspend fun insert(info: RecurringExpenseInfo) {
        withContext(Dispatchers.IO) {
            database.recurringExpenseDao().insert(info)
        }
    }

    fun refresh(): LiveData<List<RecurringExpenseInfo>> {
        return database.recurringExpenseDao().readReactive()

    }

    fun refresh(before: Long): LiveData<List<RecurringExpenseInfo>> {
        return database.recurringExpenseDao().readReactive(before)
    }

    suspend fun update(id: Int, recurOn: Long) {
        withContext(Dispatchers.IO) {
            database.recurringExpenseDao().update(id, recurOn)
        }
    }

    suspend fun delete(id: Int) {
        withContext(Dispatchers.IO) {
            database.recurringExpenseDao().delete(id)
        }
    }

    suspend fun delete(identifier: String) {
        withContext(Dispatchers.IO) {
            database.recurringExpenseDao().delete(identifier)
        }
    }

    suspend fun delete() {
        withContext(Dispatchers.IO) {
            database.recurringExpenseDao().delete()
        }
    }
}
package com.ramitsuri.expensemanager.utils

import androidx.annotation.WorkerThread
import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.data.dao.ExpenseDao
import com.ramitsuri.expensemanager.data.dao.RecurringExpenseInfoDao
import com.ramitsuri.expensemanager.entities.Expense
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Manages the logic for adding new copies of a recurring Expense
 *
 * Steps
 * 1. Read all RecurringExpenseInfo
 * 2. Check for each, if can recur via canRecur()
 * 3. If yes, get the new copy of Expense via getExpenses()
 * (The above method returns a list because there could be a scenario when the recurring expenses
 * weren't added. The method would return a list of valid expenses that should be added)
 * 4. Save the new copy of expenses
 * 5. Update RecurringExpenseInfo for that Expense via updateRecurringExpenseInfo()
 * 6. Save RecurringExpenseInfo
 */
class RecurringExpenseManager(private val expenseDao: ExpenseDao,
        private val recurringDao: RecurringExpenseInfoDao) {

    fun canRecur(info: RecurringExpenseInfo,
            currentTimeInMillis: Long,
            zoneId: ZoneId): Boolean {
        var canRecur = false

        val currentTime = getZonedDateTime(currentTimeInMillis, zoneId)
        val lastOccurrenceTime = getZonedDateTime(info.lastOccur, zoneId)
        val requestedRecurTime = getRequestedRecurTime(lastOccurrenceTime, info.recurType)

        if (requestedRecurTime == null) {
            canRecur = false
        } else if (requestedRecurTime.isBefore(currentTime) ||
                requestedRecurTime.isEqual(currentTime)) {
            canRecur = true
        }

        return canRecur
    }

    fun getExpenses(expense: Expense?,
            info: RecurringExpenseInfo,
            currentTimeInMillis: Long,
            zoneId: ZoneId): List<Expense> {
        if (expense == null) {
            return mutableListOf()
        }
        val list = mutableListOf<Expense>()
        var recurringInfo = info.copy()
        while (canRecur(recurringInfo, currentTimeInMillis, zoneId)) {
            val newExpense = Expense(expense)
            newExpense.generateIdentifier()
            newExpense.isSynced = false
            newExpense.addType = AddType.RECUR

            // DateTime held in the Expense that's being used to recreate the recurring expense
            // might be old as that expense is the first one that was requested to be recurring.
            // There might have been other copies of it, automatically added via the
            // recurring feature.
            // So DateTime needs to be read from RecurringExpenseInfo as that represents
            // the last occurrence.
            val lastOccurrenceTime = getZonedDateTime(info.lastOccur, zoneId)
            val requestedRecurTime = getRequestedRecurTime(lastOccurrenceTime, info.recurType)
            newExpense.dateTime =
                    requestedRecurTime?.toInstant()?.toEpochMilli() ?: expense.dateTime
            list.add(newExpense)
            recurringInfo = updateRecurringExpenseInfo(newExpense, info)
        }

        return list
    }

    fun updateRecurringExpenseInfo(newExpenses: List<Expense>,
            info: RecurringExpenseInfo): RecurringExpenseInfo {
        var latestOccurrence = info.lastOccur
        for (newExpense in newExpenses) {
            if (newExpense.dateTime >= latestOccurrence) {
                latestOccurrence = newExpense.dateTime
            }
        }
        info.lastOccur = latestOccurrence
        return info
    }

    /**
     * Since it does some database operations, this method should be run in a background thread so
     * as to not block the main thread.
     * Returns number of new expenses that were added automatically as a result of the job.
     */
    @WorkerThread
    fun process(timeZoneId: ZoneId, currentTimeInMillis: Long): Int {
        val recurringExpenses = recurringDao.read()
        val newExpenses = mutableListOf<Expense>()
        val updatedRecurringExpenses = mutableListOf<RecurringExpenseInfo>()
        for (recurringExpense in recurringExpenses) {
            if (canRecur(recurringExpense, currentTimeInMillis, timeZoneId)) {
                val expense: Expense? = expenseDao.getExpense(recurringExpense.identifier)
                val expenses =
                        getExpenses(expense, recurringExpense, currentTimeInMillis, timeZoneId)
                newExpenses.addAll(expenses)
                updatedRecurringExpenses.add(updateRecurringExpenseInfo(expenses, recurringExpense))
            }
        }

        if (newExpenses.size > 0) {
            // Save expenses
            expenseDao.insert(newExpenses)
            // Update recurring expenses
            recurringDao.update(updatedRecurringExpenses)
        }
        return newExpenses.size
    }

    private fun updateRecurringExpenseInfo(newExpense: Expense,
            info: RecurringExpenseInfo): RecurringExpenseInfo {
        info.lastOccur = newExpense.dateTime
        return info
    }

    private fun getRequestedRecurTime(lastOccurrenceTime: ZonedDateTime,
            @RecurType
            recurType: String): ZonedDateTime? {
        var requestedRecurTime: ZonedDateTime? = null
        when (recurType) {
            RecurType.DAILY -> {
                requestedRecurTime = lastOccurrenceTime.plusDays(1)
            }
            RecurType.WEEKLY -> {
                requestedRecurTime = lastOccurrenceTime.plusWeeks(1)
            }
            RecurType.MONTHLY -> {
                requestedRecurTime = lastOccurrenceTime.plusMonths(1)
            }
        }
        return requestedRecurTime
    }

    private fun getZonedDateTime(timeInMillis: Long,
            zoneId: ZoneId): ZonedDateTime {
        return DateHelper.getZonedDateTime(timeInMillis, zoneId)
    }
}
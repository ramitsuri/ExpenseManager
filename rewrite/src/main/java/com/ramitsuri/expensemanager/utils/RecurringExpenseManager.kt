package com.ramitsuri.expensemanager.utils

import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.entities.Expense
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RecurringExpenseManager() {
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

    fun getRecurringExpense(expense: Expense, currentTimeInMillis: Long): Expense {
        val newExpense = Expense()
        newExpense.recordType = expense.recordType
        newExpense.dateTime = currentTimeInMillis
        return newExpense
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
        val instant = Instant.ofEpochMilli(timeInMillis)
        return ZonedDateTime.ofInstant(instant, zoneId)
    }
}
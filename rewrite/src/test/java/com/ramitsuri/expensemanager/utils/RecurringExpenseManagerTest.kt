package com.ramitsuri.expensemanager.utils

import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.entities.Expense
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RecurringExpenseManagerTest {

    private val zoneId = ZoneId.of("America/New_York")

    @Test
    fun testCanRecur_monthly_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertTrue(RecurringExpenseManager()
                .canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testCanRecur_moreThanOneMonthDifference_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).minusSeconds(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertTrue(RecurringExpenseManager()
                .canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testCanRecur_lessThanOneMonthDifference_shouldReturnFalse() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).plusSeconds(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertFalse(RecurringExpenseManager()
                .canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testGetExpenses_syncedShouldBeFalse() {
        val currentTime = Instant.now().toEpochMilli()
        val expense = Expense()
        expense.isSynced = true

        val recurringExpenseInfo = RecurringExpenseInfo()

        val newExpenses = RecurringExpenseManager()
                .getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

        for (newExpense in newExpenses) {
            assertFalse(newExpense.isSynced)
        }
    }

    @Test
    fun testGetExpenses_addTypeShouldBeRecur() {
        val currentTime = Instant.now().toEpochMilli()
        val expense = Expense()
        val recurringExpenseInfo = RecurringExpenseInfo()

        val newExpenses = RecurringExpenseManager()
                .getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

        for (newExpense in newExpenses) {
            assertEquals(AddType.RECUR, newExpense.addType)
        }
    }

    @Test
    fun testGetExpenses_dateTimeShouldBe_aMonthFromNow_ifMultipleOccurrences() {
        val currentTime = 1620996909000L // Represents May 14 2021 08:55:09 in NewYork time zone
        val dateTimeInitial = 1607954109000L // Represents Dec 14 2020 08:55:09 in NewYork time zone

        // Represents date times in 1 month interval from dateTimeInitial to currentTime
        val recurDateTimes =
                listOf(1610632509000L, 1613310909000L, 1615726509000L, 1618404909000L, currentTime)

        val expense = Expense()
        expense.dateTime = dateTimeInitial

        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = expense.dateTime

        val newExpenses = RecurringExpenseManager()
                .getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

        assertEquals(5, newExpenses.size)
        for (index in 0..4) {
            assertEquals(recurDateTimes[index], newExpenses[index].dateTime)
        }
    }

    @Test
    fun testGetExpenses_dateTimeShouldBe_aMonthFromNow_ifSingleOccurrence() {
        val currentTime = 1610632509000L // Represents May 14 2021 08:55:09 in NewYork time zone
        val dateTimeInitial = 1607954109000L // Represents Dec 14 2020 08:55:09 in NewYork time zone

        val expense = Expense()
        expense.dateTime = dateTimeInitial

        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = expense.dateTime

        val newExpenses = RecurringExpenseManager()
                .getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

        assertEquals(1, newExpenses.size)
        assertEquals(currentTime, newExpenses[0].dateTime)

    }

    @Test
    fun testUpdateRecurringExpenseInfo_lastOccurShouldBe_sameAsExpenseDateTime() {
        val currentTime = 1620996909000L // Represents May 14 2021 08:55:09 in NewYork time zone
        val dateTimeInitial = 1607954109000L // Represents Dec 14 2020 08:55:09 in NewYork time zone
        val recurringManager = RecurringExpenseManager()

        val expense = Expense()
        expense.dateTime = dateTimeInitial

        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = expense.dateTime

        val newExpenses = recurringManager
                .getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)
        val updatedRecurringExpenseInfo =
                recurringManager.updateRecurringExpenseInfo(newExpenses,
                        recurringExpenseInfo)

        assertEquals(currentTime, updatedRecurringExpenseInfo.lastOccur)
    }
}
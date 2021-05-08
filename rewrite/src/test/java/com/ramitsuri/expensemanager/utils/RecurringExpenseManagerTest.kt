package com.ramitsuri.expensemanager.utils

import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.data.dao.ExpenseDao
import com.ramitsuri.expensemanager.data.dao.RecurringExpenseInfoDao
import com.ramitsuri.expensemanager.entities.Expense
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import com.ramitsuri.expensemanager.testUtils.MockExpenseDao
import com.ramitsuri.expensemanager.testUtils.MockRecurringExpenseInfoDao
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RecurringExpenseManagerTest {

    private val zoneId = ZoneId.of("America/New_York")
    private val expenseDao: ExpenseDao = MockExpenseDao()
    private val recurringDao: RecurringExpenseInfoDao = MockRecurringExpenseInfoDao()
    private val manager = RecurringExpenseManager(expenseDao, recurringDao)

    private val dateTime: Long = 1609354800000L // 30 Dec 2020 19:00:00 UTC
    private val ONE_DAY: Long = 24 * 60 * 60 * 1000
    private val ONE_WEEK: Long = ONE_DAY * 7

    @Test
    fun testCanRecur_monthly_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertTrue(manager.canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testCanRecur_moreThanOneMonthDifference_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).minusSeconds(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertTrue(manager.canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testCanRecur_lessThanOneMonthDifference_shouldReturnFalse() {
        val currentTime = Instant.now()
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).plusDays(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        assertFalse(manager.canRecur(recurringExpenseInfo, currentTime.toEpochMilli(), zoneId))
    }

    @Test
    fun testGetExpenses_addTypeShouldBeRecur() {
        val currentTime = Instant.now().toEpochMilli()
        val expense = Expense()
        val recurringExpenseInfo = RecurringExpenseInfo()

        val newExpenses = manager.getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

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

        val newExpenses = manager.getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

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

        val newExpenses = manager.getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)

        assertEquals(1, newExpenses.size)
        assertEquals(currentTime, newExpenses[0].dateTime)
    }

    @Test
    fun testUpdateRecurringExpenseInfo_lastOccurShouldBe_sameAsExpenseDateTime() {
        val currentTime = 1620996909000L // Represents May 14 2021 08:55:09 in NewYork time zone
        val dateTimeInitial = 1607954109000L // Represents Dec 14 2020 08:55:09 in NewYork time zone

        val expense = Expense()
        expense.dateTime = dateTimeInitial

        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = expense.dateTime

        val newExpenses = manager.getExpenses(expense, recurringExpenseInfo, currentTime, zoneId)
        val updatedRecurringExpenseInfo =
                manager.updateRecurringExpenseInfo(newExpenses, recurringExpenseInfo)

        assertEquals(currentTime, updatedRecurringExpenseInfo.lastOccur)
    }

    @Test
    fun testProcess_shouldAddExpense_ifDailyAndCurrentMoreThanDueTime() {
        val expense = Expense()
        expense.identifier = "1"
        expense.amount = BigDecimal("0.01")
        expense.dateTime = dateTime
        expenseDao.insert(expense)

        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.DAILY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = dateTime + ONE_DAY
        val currentTime = dueTime + 1

        assertTrue(manager.process(zoneId, currentTime) > 0)
        assertEquals(2, expenseDao.expenses.size)
        assertEquals(dueTime, recurringDao.read()[0].lastOccur)
    }

    @Test
    fun testProcess_shouldAddExpense_ifWeeklyAndCurrentMoreThanDueTime() {
        val expense = Expense()
        expense.identifier = "1"
        expense.amount = BigDecimal("0.01")
        expense.dateTime = dateTime
        expenseDao.insert(expense)

        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.WEEKLY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = dateTime + ONE_WEEK
        val currentTime = dueTime + 1

        assertTrue(manager.process(zoneId, currentTime) > 0)
        assertEquals(2, expenseDao.expenses.size)
        assertEquals(dueTime, recurringDao.read()[0].lastOccur)
    }

    @Test
    fun testProcess_shouldAddExpense_ifMonthlyAndCurrentMoreThanDueTime() {
        val expense = Expense()
        expense.identifier = "1"
        expense.amount = BigDecimal("0.01")
        expense.dateTime = dateTime
        expenseDao.insert(expense)

        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.MONTHLY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), zoneId)
                .plusMonths(1)
        val currentTime = dueTime
                .plusSeconds(1)
        assertTrue(manager.process(zoneId, currentTime.toInstant().toEpochMilli()) > 0)
        assertEquals(2, expenseDao.expenses.size)
        assertEquals(dueTime.toInstant().toEpochMilli(), recurringDao.read()[0].lastOccur)
    }

    @Test
    fun testProcess_shouldAddExpense_ifMonthlyAndCurrentEqualToDueTime() {
        val expense = Expense()
        expense.identifier = "1"
        expense.amount = BigDecimal("0.01")
        expense.dateTime = dateTime
        expenseDao.insert(expense)

        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.MONTHLY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), zoneId)
                .plusMonths(1)
        assertTrue(manager.process(zoneId, dueTime.toInstant().toEpochMilli()) > 0)
        assertEquals(2, expenseDao.expenses.size)
        assertEquals(dueTime.toInstant().toEpochMilli(), recurringDao.read()[0].lastOccur)
    }

    @Test
    fun testProcess_shouldNotAddExpense_ifMonthlyAndCurrentLessThanDueTime() {
        val expense = Expense()
        expense.identifier = "1"
        expense.amount = BigDecimal("0.01")
        expense.dateTime = dateTime
        expenseDao.insert(expense)

        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.MONTHLY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), zoneId)
                .plusMonths(1)
        val currentTime = dueTime
                .minusSeconds(1)
        assertFalse(manager.process(zoneId, currentTime.toInstant().toEpochMilli()) > 0)
        assertEquals(1, expenseDao.expenses.size)
    }

    @Test
    fun testProcess_shouldNotAddExpense_ifNoExpenses() {
        val recurringExpense = RecurringExpenseInfo()
        recurringExpense.recurType = RecurType.MONTHLY
        recurringExpense.identifier = "1"
        recurringExpense.lastOccur = dateTime
        recurringDao.insert(recurringExpense)

        val dueTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), zoneId)
                .plusMonths(1)
        val currentTime = dueTime
        assertFalse(manager.process(zoneId, currentTime.toInstant().toEpochMilli()) > 0)
    }
}
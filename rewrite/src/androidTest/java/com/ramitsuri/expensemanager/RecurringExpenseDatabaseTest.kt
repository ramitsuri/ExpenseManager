package com.ramitsuri.expensemanager

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.data.dao.RecurringExpenseInfoDao
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RecurringExpenseDatabaseTest: BaseDatabaseTest() {
    private lateinit var dao: RecurringExpenseInfoDao

    private val dateTime: Long = 1609354800000L // 30 Dec 2020 19:00:00 UTC

    @Before
    override fun createDb() {
        super.createDb()
        dao = mDb.recurringExpenseDao()
    }

    @Test
    fun testInsert() {
        for (info in getRecurringExpenses()) {
            assertTrue(dao.insert(info) > 0L)
        }
    }

    @Test
    fun testRead() {
        insertAll(getRecurringExpenses())

        val actualExpenses = dao.read()
        assertEquals(getRecurringExpenses().size, actualExpenses.size)

        val actualExpensesBefore = dao.read(dateTime)
        assertEquals(3, actualExpensesBefore.size)
    }

    @Test
    fun testDelete() {
        insertAll(getRecurringExpenses())

        dao.delete("1")
        assertEquals(getRecurringExpenses().size - 1, dao.read().size)

        dao.delete()
        assertEquals(0, dao.read().size)
    }

    @Test
    fun testUpdate() {
        insertAll(getRecurringExpenses())

        val updatedRecurringExpenses = dao.read()
        for (info in updatedRecurringExpenses) {
            info.lastOccur = dateTime
        }

        dao.update(updatedRecurringExpenses)

        for (info in dao.read()) {
            assertEquals(dateTime, info.lastOccur)
        }
    }

    @Test
    fun testUpdateRecur() {
        insertAll(getRecurringExpenses())

        val updatedRecurringExpenses = dao.read()
        for (info in updatedRecurringExpenses) {
            dao.update(info.id, RecurType.NONE)
        }

        for (info in dao.read()) {
            assertEquals(RecurType.NONE, info.recurType)
        }
    }

    @Test
    fun testInsertUpdateOrDelete_shouldInsert_ifNotExistsAndRecurIsNotNone() {
        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "1"
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = dateTime

        dao.insertUpdateOrDelete(recurringExpenseInfo)

        val actual = dao.read()[0]
        assertEquals(recurringExpenseInfo.identifier, actual.identifier)
        assertEquals(recurringExpenseInfo.lastOccur, actual.lastOccur)
        assertEquals(recurringExpenseInfo.recurType, actual.recurType)
        assertEquals(1, dao.read().size)
    }

    @Test
    fun testInsertUpdateOrDelete_shouldNotInsert_ifNotExistsAndRecurIsNone() {
        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "1"
        recurringExpenseInfo.recurType = RecurType.NONE
        recurringExpenseInfo.lastOccur = dateTime

        dao.insertUpdateOrDelete(recurringExpenseInfo)

        assertTrue(dao.read().isEmpty())
    }

    @Test
    fun testInsertUpdateOrDelete_shouldDelete_ifExistsAndRecurIsNone() {
        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "1"
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = dateTime
        dao.insert(recurringExpenseInfo)

        recurringExpenseInfo.recurType = RecurType.NONE
        dao.insertUpdateOrDelete(recurringExpenseInfo)

        assertNull(dao.read(recurringExpenseInfo.identifier))
        assertTrue(dao.read().isEmpty())
    }

    @Test
    fun testInsertUpdateOrDelete_shouldUpdate_ifExistsAndRecurIsNotNone() {
        val recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "1"
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = dateTime
        dao.insert(recurringExpenseInfo)

        recurringExpenseInfo.recurType = RecurType.WEEKLY
        dao.insertUpdateOrDelete(recurringExpenseInfo)

        val actual = dao.read()[0]
        assertEquals(recurringExpenseInfo.identifier, actual.identifier)
        assertEquals(recurringExpenseInfo.lastOccur, actual.lastOccur)
        assertEquals(recurringExpenseInfo.recurType, actual.recurType)
        assertEquals(1, dao.read().size)
    }

    private fun insertAll(recurringExpenses: List<RecurringExpenseInfo>) {
        for (info in recurringExpenses) {
            dao.insert(info)
        }
    }

    private fun getRecurringExpenses(): List<RecurringExpenseInfo> {
        val recurringExpenses = mutableListOf<RecurringExpenseInfo>()

        var recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "1"
        recurringExpenseInfo.recurType = RecurType.DAILY
        recurringExpenseInfo.lastOccur = dateTime - 1
        recurringExpenses.add(recurringExpenseInfo)

        recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "2"
        recurringExpenseInfo.recurType = RecurType.MONTHLY
        recurringExpenseInfo.lastOccur = dateTime
        recurringExpenses.add(recurringExpenseInfo)

        recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "3"
        recurringExpenseInfo.recurType = RecurType.WEEKLY
        recurringExpenseInfo.lastOccur = dateTime + 1
        recurringExpenses.add(recurringExpenseInfo)

        recurringExpenseInfo = RecurringExpenseInfo()
        recurringExpenseInfo.identifier = "4"
        recurringExpenseInfo.recurType = RecurType.DAILY
        recurringExpenseInfo.lastOccur = dateTime - 2
        recurringExpenses.add(recurringExpenseInfo)

        return recurringExpenses
    }
}
package com.ramitsuri.expensemanager.entities

import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecordType
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ExpenseUnitTest {
    private val dateTime = 1L
    private val amount = "4.5"
    private val paymentMethod = "paymentMethod"
    private val category = "category"
    private val description = "description"
    private val store = "store"
    private val isStarred = "FLAG"
    private val isIncome = "INCOME"
    private val recordType = RecordType.MONTHLY
    private val identifier = "1"
    private val addType = AddType.MANUAL

    @Test
    fun testObjectListConstructor_shouldInstantiateWithAllValues() {
        val list = mutableListOf<Any>()
        list.add(dateTime)
        list.add(description)
        list.add(store)
        list.add(amount)
        list.add(paymentMethod)
        list.add(category)
        list.add(recordType)
        list.add(identifier)
        list.add(addType)
        list.add(isStarred)
        list.add(isIncome)
        val expense = Expense(list)

        assertEquals(dateTime, expense.dateTime)
        assertEquals(BigDecimal(amount).compareTo(expense.amount), 0)
        assertEquals(paymentMethod, expense.paymentMethod)
        assertEquals(category, expense.category)
        assertEquals(description, expense.description)
        assertEquals(store, expense.store)
        assertTrue(expense.isStarred)
        assertFalse(expense.isSynced)
        assertEquals(0, expense.sheetId)
        assertTrue(expense.isIncome)
        assertEquals(recordType, expense.recordType)
        assertEquals(identifier, expense.identifier)
        assertEquals(addType, expense.addType)
    }

    @Test
    fun testObjectListConstructor_shouldInstantiateWithFewValues() {
        val list = mutableListOf<Any>()
        list.add(dateTime)
        list.add(description)
        list.add(store)
        list.add(amount)
        list.add(paymentMethod)
        list.add(category)
        list.add(recordType)
        list.add(identifier)
        list.add(addType)
        val expense = Expense(list)

        assertEquals(dateTime, expense.dateTime)
        assertEquals(BigDecimal(amount).compareTo(expense.amount), 0)
        assertEquals(paymentMethod, expense.paymentMethod)
        assertEquals(category, expense.category)
        assertEquals(description, expense.description)
        assertEquals(store, expense.store)
        assertFalse(expense.isStarred)
        assertFalse(expense.isSynced)
        assertEquals(0, expense.sheetId)
        assertFalse(expense.isIncome)
        assertEquals(recordType, expense.recordType)
        assertEquals(identifier, expense.identifier)
        assertEquals(addType, expense.addType)
    }

    @Test
    fun testToList_isStarred_isIncome() {
        val expense = Expense(
                dateTime,
                BigDecimal(amount),
                paymentMethod,
                category,
                description,
                store,
                true,
                true,
                0,
                true,
                recordType,
                identifier,
                addType)

        val list = expense.toStringList()
        assertEquals(11, list.size)
        assertEquals(dateTime.toString(), list[0])
        assertEquals(description, list[1])
        assertEquals(store, list[2])
        assertEquals(amount, list[3])
        assertEquals(paymentMethod, list[4])
        assertEquals(category, list[5])
        assertEquals(recordType, list[6])
        assertEquals(identifier, list[7])
        assertEquals(addType, list[8])
        assertEquals(isStarred, list[9])
        assertEquals(isIncome, list[10])
    }

    @Test
    fun testToList_isNotIncome() {
        val expense = Expense(
                dateTime,
                BigDecimal(amount),
                paymentMethod,
                category,
                description,
                store,
                true,
                true,
                0,
                false,
                recordType,
                identifier,
                addType)

        val list = expense.toStringList()
        assertEquals(11, list.size)
        assertEquals(dateTime.toString(), list[0])
        assertEquals(description, list[1])
        assertEquals(store, list[2])
        assertEquals(amount, list[3])
        assertEquals(paymentMethod, list[4])
        assertEquals(category, list[5])
        assertEquals(recordType, list[6])
        assertEquals(identifier, list[7])
        assertEquals(addType, list[8])
        assertEquals(isStarred, list[9])
        assertEquals("", list[10])
    }

    @Test
    fun testToList_isNotStarred() {
        val expense = Expense(
                dateTime,
                BigDecimal(amount),
                paymentMethod,
                category,
                description,
                store,
                true,
                false,
                0,
                true,
                recordType,
                identifier,
                addType)

        val list = expense.toStringList()
        assertEquals(11, list.size)
        assertEquals(dateTime.toString(), list[0])
        assertEquals(description, list[1])
        assertEquals(store, list[2])
        assertEquals(amount, list[3])
        assertEquals(paymentMethod, list[4])
        assertEquals(category, list[5])
        assertEquals(recordType, list[6])
        assertEquals(identifier, list[7])
        assertEquals(addType, list[8])
        assertEquals("", list[9])
        assertEquals(isIncome, list[10])
    }

    @Test
    fun testToList_isNotIncome_isNotStarred() {
        val expense = Expense(
                dateTime,
                BigDecimal(amount),
                paymentMethod,
                category,
                description,
                store,
                true,
                false,
                0,
                false,
                recordType,
                identifier,
                addType)

        val list = expense.toStringList()
        assertEquals(11, list.size)
        assertEquals(dateTime.toString(), list[0])
        assertEquals(description, list[1])
        assertEquals(store, list[2])
        assertEquals(amount, list[3])
        assertEquals(paymentMethod, list[4])
        assertEquals(category, list[5])
        assertEquals(recordType, list[6])
        assertEquals(identifier, list[7])
        assertEquals(addType, list[8])
        assertEquals("", list[9])
        assertEquals("", list[10])
    }
}
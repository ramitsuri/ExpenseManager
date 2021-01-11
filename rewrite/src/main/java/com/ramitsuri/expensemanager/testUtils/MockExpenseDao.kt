package com.ramitsuri.expensemanager.testUtils

import androidx.sqlite.db.SupportSQLiteQuery
import com.ramitsuri.expensemanager.data.dao.ExpenseDao
import com.ramitsuri.expensemanager.entities.Expense

class MockExpenseDao: ExpenseDao() {
    private val values = mutableListOf<Expense>()
    private var autoIncrementId = 1

    override fun getExpenses(): MutableList<Expense> {
        return values
    }

    override fun getExpensesForDateRange(fromDateTime: Long,
            toDateTime: Long): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getAllStarred(): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getAllUnsynced(): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getForQuery(query: SupportSQLiteQuery?): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getExpense(id: Long): Expense? {
        for (expense in values) {
            if (expense.id.toLong() == id) {
                return expense
            }
        }
        return null
    }

    override fun getExpense(identifier: String?): Expense? {
        for (expense in values) {
            if (expense.identifier == identifier) {
                return expense
            }
        }
        return null
    }

    override fun getIncomes(): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getStores(startsWith: String?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun getCategories(): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun getPaymentMethods(): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun getForStore(store: String?): Expense {
        TODO("Not yet implemented")
    }

    override fun getForEmptyIdentifier(): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getForRecordType(recordType: String?): MutableList<Expense> {
        TODO("Not yet implemented")
    }

    override fun getDateTimes(): MutableList<Long> {
        TODO("Not yet implemented")
    }

    override fun insert(expense: Expense): Long {
        val copy = expense.copy()
        copy.id = autoIncrementId
        values.add(copy)
        autoIncrementId++
        return copy.id.toLong()
    }

    override fun insert(expenses: MutableList<Expense>?) {
        expenses?.let {
            for (expense in expenses) {
                insert(expense)
            }
        }
    }

    override fun updateUnsynced() {
        TODO("Not yet implemented")
    }

    override fun updateSetAllUnsynced() {
        TODO("Not yet implemented")
    }

    override fun setStarred(id: Int) {
        TODO("Not yet implemented")
    }

    override fun setUnstarred(id: Int) {
        TODO("Not yet implemented")
    }

    override fun updateSetUnsyncedForQuery(query: SupportSQLiteQuery?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteExpense(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun deleteSynced() {
        TODO("Not yet implemented")
    }
}
package com.ramitsuri.expensemanager.testUtils

import androidx.lifecycle.LiveData
import com.ramitsuri.expensemanager.data.dao.RecurringExpenseInfoDao
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo

class MockRecurringExpenseInfoDao: RecurringExpenseInfoDao() {
    private val values = mutableListOf<RecurringExpenseInfo>()
    private var autoIncrementId = 1

    override fun insert(info: RecurringExpenseInfo): Long {
        val copy = info.copy()
        copy.id = autoIncrementId
        values.add(copy)
        autoIncrementId++
        return copy.id.toLong()
    }

    override fun readReactive(): LiveData<List<RecurringExpenseInfo>> {
        TODO("Not yet implemented")
    }

    override fun readReactive(before: Long): LiveData<List<RecurringExpenseInfo>> {
        TODO("Not yet implemented")
    }

    override fun readReactive(identifier: String): LiveData<RecurringExpenseInfo?> {
        TODO("Not yet implemented")
    }

    override fun read(): List<RecurringExpenseInfo> {
        return values
    }

    override fun read(before: Long): List<RecurringExpenseInfo> {
        val filteredValues = mutableListOf<RecurringExpenseInfo>()
        for (info in values) {
            if (info.lastOccur <= before) {
                filteredValues.add(info)
            }
        }
        return filteredValues
    }

    override fun read(identifier: String): RecurringExpenseInfo? {
        TODO("Not yet implemented")
    }

    override fun update(id: Int, recurType: Long) {
        for (info in values) {
            if (info.id == id) {
                info.lastOccur = recurType
                break
            }
        }
    }

    override fun update(id: Int, recurType: String) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        val iterator = values.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().id == id) {
                iterator.remove()
                break
            }
        }
    }

    override fun delete(identifier: String) {
        val iterator = values.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().identifier == identifier) {
                iterator.remove()
                break
            }
        }
    }

    override fun delete() {
        values.clear()
    }
}
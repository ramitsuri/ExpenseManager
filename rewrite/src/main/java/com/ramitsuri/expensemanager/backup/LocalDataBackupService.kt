package com.ramitsuri.expensemanager.backup

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase
import com.ramitsuri.expensemanager.data.dao.ExpenseDao
import com.ramitsuri.expensemanager.entities.Filter
import com.ramitsuri.expensemanager.work.WorkResult
import java.time.Month
import kotlin.math.exp

class LocalDataBackupService(private val database: ExpenseManagerDatabase): DataBackupService {

    private val jsonProcessor = Gson()

    @WorkerThread
    override fun process(): WorkResult<String> {
        database.openHelper.readableDatabase.version
        val expenseData = getExpenseData(database.expenseDao())

        val backupData = BackupData(
                database.openHelper.readableDatabase.version,
                expenseData,
                database.categoryDao().all,
                database.paymentMethodDao().all,
                database.budgetDao().all,
                database.logDao().all,
                database.recurringExpenseDao().getAll())
        val result = jsonProcessor.toJson(backupData)
        return WorkResult.success(result)
    }

    @WorkerThread
    private fun getExpenseData(expenseDao: ExpenseDao): List<ExpenseYear> {
        val expenseYears = mutableListOf<ExpenseYear>()
        for (year in 2012..2022) {
            val expenseMonths = mutableListOf<ExpenseMonth>()
            var countYear = 0
            for (month in 1..12) {
                val filter = Filter()
                filter.addYear(year)
                filter.addMonth(month)
                val expenses = expenseDao.getForFilter(filter)
                if (expenses != null && expenses.isNotEmpty()) {
                    countYear += expenses.size
                    expenseMonths.add(ExpenseMonth(getMonth(month), expenses.size, expenses))
                }
            }
            if (countYear != 0) {
                expenseYears.add(ExpenseYear(year, countYear, expenseMonths))
            }
        }
        return expenseYears
    }

    private fun getMonth(month: Int): String {
        return Month.of(month).toString()
    }
}
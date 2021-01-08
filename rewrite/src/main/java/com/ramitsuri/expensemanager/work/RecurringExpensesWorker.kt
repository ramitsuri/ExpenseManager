package com.ramitsuri.expensemanager.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ramitsuri.expensemanager.constants.Constants
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase
import com.ramitsuri.expensemanager.utils.AppHelper
import com.ramitsuri.expensemanager.utils.RecurringExpenseManager
import java.time.Instant

class RecurringExpensesWorker(context: Context, workerParams: WorkerParameters):
        BaseWorker(context, workerParams) {

    override fun doWork(): Result {
        val workType = inputData.getString(Constants.Work.TYPE)
        try {
            val database = ExpenseManagerDatabase.getInstance()
            val manager =
                    RecurringExpenseManager(database.expenseDao(), database.recurringExpenseDao())
            val timeZoneId = AppHelper.getTimeZone().toZoneId()
            val currentTimeInMillis = Instant.now().toEpochMilli()

            val numOfNewExpenses = manager.process(timeZoneId, currentTimeInMillis)
            return if (numOfNewExpenses > 0) {
                onSuccess(workType, "Expenses added automatically: $numOfNewExpenses")
                Result.success()
            } else {
                onFailure(workType, "No expenses added")
                Result.failure()
            }
        } catch (e: Exception) {
            onFailure(workType, "Failed due to $e")
            return Result.failure()
        }
    }
}
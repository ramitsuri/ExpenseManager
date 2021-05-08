package com.ramitsuri.expensemanager.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ramitsuri.expensemanager.MainApplication
import com.ramitsuri.expensemanager.constants.Constants

class ExpensesBackupWorker(context: Context, workerParams: WorkerParameters):
        BaseWorker(context, workerParams) {
    override fun doWork(): Result {
        val workType = inputData.getString(Constants.Work.TYPE)
        val backupService = MainApplication.getInstance().injector.provideExpenseBackupService()

        return when (val result = backupService.process()) {
            is WorkResult.Success -> {
                onSuccess(workType, result.data)
                Result.success()
            }
            is WorkResult.Failure -> {
                onFailure(workType, result.data)
                Result.failure()
            }
            else -> {
                Result.failure()
            }
        }
    }
}
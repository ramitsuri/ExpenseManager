    package com.ramitsuri.expensemanager.backup

import androidx.annotation.WorkerThread

interface ExpenseBackupService {
    @WorkerThread
    fun process(): WorkResult<String>
}
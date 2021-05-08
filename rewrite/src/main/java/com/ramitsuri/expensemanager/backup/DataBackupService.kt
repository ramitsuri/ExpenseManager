package com.ramitsuri.expensemanager.backup

import androidx.annotation.WorkerThread
import com.ramitsuri.expensemanager.work.WorkResult

interface DataBackupService {
    @WorkerThread
    fun process(): WorkResult<String>
}
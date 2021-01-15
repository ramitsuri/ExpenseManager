package com.ramitsuri.expensemanager.dependency

import com.ramitsuri.expensemanager.backup.ExpenseBackupService
import com.ramitsuri.expensemanager.backup.WorkResult

class Injector {
    fun provideExpenseBackupService(): ExpenseBackupService = object : ExpenseBackupService {
        override fun process(): WorkResult<String> {
            TODO("Not yet implemented")
        }
    }
}
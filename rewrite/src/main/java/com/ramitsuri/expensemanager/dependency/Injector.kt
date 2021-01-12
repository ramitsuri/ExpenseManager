package com.ramitsuri.expensemanager.dependency

import com.ramitsuri.expensemanager.backup.ExpenseBackupService
import com.ramitsuri.expensemanager.backup.SpreadsheetExpenseBackupService

class Injector {
    fun provideExpenseBackupService(): ExpenseBackupService = SpreadsheetExpenseBackupService()
}
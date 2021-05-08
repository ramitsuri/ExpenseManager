package com.ramitsuri.expensemanager.dependency

import com.ramitsuri.expensemanager.backup.DataBackupService
import com.ramitsuri.expensemanager.backup.LocalDataBackupService
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase

class Injector {
    fun provideDatabase(): ExpenseManagerDatabase = ExpenseManagerDatabase.getInstance()
    fun provideExpenseBackupService(): DataBackupService = LocalDataBackupService(provideDatabase())
}
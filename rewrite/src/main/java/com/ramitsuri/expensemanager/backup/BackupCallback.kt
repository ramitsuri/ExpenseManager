package com.ramitsuri.expensemanager.backup

interface BackupCallback {
    fun onSuccess()

    fun onFailed(errorMessage: String)
}
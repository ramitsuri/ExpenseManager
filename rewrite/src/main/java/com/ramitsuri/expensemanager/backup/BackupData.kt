package com.ramitsuri.expensemanager.backup

import com.ramitsuri.expensemanager.entities.*

data class BackupData(
        val dbVersion: Int,
        val expenses: List<ExpenseYear>,
        val categories: List<Category>,
        val paymentMethods: List<PaymentMethod>,
        val budgets: List<Budget>,
        val logs: List<Log>,
        val recurringExpenses: List<RecurringExpenseInfo>
)

data class ExpenseYear(val year: Int, val count: Int, val expenseMonths: List<ExpenseMonth>)

data class ExpenseMonth(val month: String, val count: Int, val expenses: List<Expense>)
package com.ramitsuri.expensemanager.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramitsuri.expensemanager.MainApplication
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import kotlinx.coroutines.launch

class RecurringExpenseViewModel : ViewModel() {
    private val mRepository = MainApplication.getInstance().recurringRepo

    private val mExpenses = mRepository.refresh()

    fun getRecurringExpenses(): LiveData<List<RecurringExpenseInfo>> {
        return mRepository.refresh()
    }

    fun add(info: RecurringExpenseInfo) {
        viewModelScope.launch {
            mRepository.suspendInsert(info)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            mRepository.delete(id)
        }
    }

    fun delete(identifier: String) {
        viewModelScope.launch {
            mRepository.delete(identifier)
        }
    }

    fun delete() {
        viewModelScope.launch {
            mRepository.delete()
        }
    }
}
package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.entities.Expense;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Expense mExpense;

    public ViewModelFactory(Expense expense) {
        mExpense = expense;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddExpenseViewModel(mExpense);
    }
}

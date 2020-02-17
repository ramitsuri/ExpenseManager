package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.entities.Expense;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Expense mExpense;
    private List<Expense> mExpenses;

    public ViewModelFactory(Expense expense) {
        mExpense = expense;
    }

    public ViewModelFactory(List<Expense> expenses) {
        mExpenses = expenses;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Expense.class).newInstance(mExpense);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            Timber.w("Cannot create instance of " + modelClass, e);
        }

        try {
            return modelClass.getConstructor(List.class).newInstance(mExpenses);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            throw new RuntimeException("Cannot create instance of " + modelClass, e);
        }
    }
}

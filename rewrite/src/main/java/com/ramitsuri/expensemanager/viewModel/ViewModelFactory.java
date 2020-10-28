package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Expense mExpense;
    private List<Expense> mExpenses;
    private Filter mFilter;

    public ViewModelFactory(Expense expense) {
        mExpense = expense;
    }

    public ViewModelFactory(List<Expense> expenses) {
        mExpenses = expenses;
    }

    public ViewModelFactory(Filter filter) {
        mFilter = filter;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Expense.class).newInstance(mExpense);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            Timber.w("Need Expense. Cannot create instance of %1s %2s", modelClass, e);
        }

        try {
            return modelClass.getConstructor(List.class).newInstance(mExpenses);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            Timber.w("Need Expense list. Cannot create instance of %1s %2s", modelClass, e);
        }

        try {
            return modelClass.getConstructor(Filter.class).newInstance(mFilter);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            Timber.w("Need Filter. Cannot create instance of %1s %2s", modelClass, e);
            throw new RuntimeException("Need Filter. Cannot create instance of " + modelClass, e);
        }
    }
}

package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.utils.SecretMessageHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class FilterOptionsViewModel extends ViewModel {

    private Filter mFilter;
    private ExpenseRepository mExpenseRepo;

    public FilterOptionsViewModel() {
        super();
        mFilter = new Filter();

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
    }

    public LiveData<List<String>> getCategories() {
        return mExpenseRepo.getCategories();
    }

    public LiveData<List<String>> getPaymentMethods() {
        return mExpenseRepo.getPaymentMethods();
    }

    public Filter onMonthPicked(int monthIndex) {
        return mFilter
                .setMonthIndex(monthIndex);
    }

    public Filter onGetIncome() {
        return mFilter
                .setIsIncome(true);
    }

    public Filter onGetStarred() {
        return mFilter
                .setStarred(true);
    }

    public Filter onCategoryPicked(@Nonnull String category) {
        List<String> categories = new ArrayList<>();
        categories.add(category);
        return mFilter
                .setCategories(categories);
    }

    public Filter onPaymentMethodPicked(@Nonnull String paymentMethod) {
        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add(paymentMethod);
        return mFilter
                .setPaymentMethods(paymentMethods);
    }

    public boolean isIncomeAvailable() {
        return SecretMessageHelper.isIncomeEnabled();
    }
}

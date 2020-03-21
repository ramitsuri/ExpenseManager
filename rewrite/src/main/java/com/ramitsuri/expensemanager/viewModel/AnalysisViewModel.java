package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.ui.adapter.BarWrapper;
import com.ramitsuri.expensemanager.utils.Calculator;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class AnalysisViewModel extends ViewModel {

    @Nullable
    private Calculator mCalculator;
    private MutableLiveData<List<BarWrapper>> mListWrappers;
    private LiveData<Boolean> mCalculated;

    private BigDecimal HUNDRED = new BigDecimal("100");

    public AnalysisViewModel(@Nonnull final List<Expense> expenses) {
        super();

        BudgetRepository budgetRepo = MainApplication.getInstance().getBudgetRepository();
        mListWrappers = new MutableLiveData<>();

        mCalculated = Transformations.map(budgetRepo.getBudgets(),
                new Function<List<Budget>, Boolean>() {
                    @Override
                    public Boolean apply(List<Budget> input) {
                        mCalculator = new Calculator(expenses, input, true, BuildConfig.DEBUG);
                        mCalculator.calculate();
                        return true;
                    }
                });
    }

    @Nullable
    public LiveData<List<BarWrapper>> getWrappers() {
        return mListWrappers;
    }

    public LiveData<Boolean> isCalculated() {
        return mCalculated;
    }

    private void preparePaymentWrappers(String allName) {
        Timber.i("Prepare payment wrappers");
        if (mCalculator == null) {
            return;
        }
        Map<String, BigDecimal> paymentValues = mCalculator.getPaymentValues();

        List<BarWrapper> wrappers = new ArrayList<>();

        // All
        BigDecimal allValue = paymentValues.get(Constants.Basic.CALCULATOR_ALL);
        wrappers.add(getPaymentOrCategoryWrapper(allName, allValue, allValue));

        for (String paymentMethod : paymentValues.keySet()) {
            if (paymentMethod.equals(Constants.Basic.CALCULATOR_ALL)) {
                continue;
            }
            BigDecimal value = paymentValues.get(paymentMethod);
            wrappers.add(getPaymentOrCategoryWrapper(paymentMethod, value, allValue));
        }
        mListWrappers.postValue(wrappers);
    }

    private void prepareCategoryWrappers(String allName) {
        Timber.i("Prepare category wrappers");
        if (mCalculator == null) {
            return;
        }
        Map<String, BigDecimal> categoryValues = mCalculator.getCategoryValues();

        List<BarWrapper> wrappers = new ArrayList<>();

        // All
        BigDecimal allValue = categoryValues.get(Constants.Basic.CALCULATOR_ALL);
        wrappers.add(getPaymentOrCategoryWrapper(allName, allValue, allValue));

        for (String category : categoryValues.keySet()) {
            if (category.equals(Constants.Basic.CALCULATOR_ALL)) {
                continue;
            }
            BigDecimal value = categoryValues.get(category);
            wrappers.add(getPaymentOrCategoryWrapper(category, value, allValue));
        }
        mListWrappers.postValue(wrappers);
    }

    private void prepareBudgetWrappers(String allName, String used, String remaining,
            String overUsed) {
        Timber.i("Prepare budget wrappers");
        if (mCalculator == null) {
            return;
        }
        List<BarWrapper> wrappers = new ArrayList<>();

        Map<String, Pair<BigDecimal, BigDecimal>> budgetValues = mCalculator.getBudgetValues();

        // All
        wrappers.add(getBudgetWrapper(allName, used, remaining, overUsed,
                budgetValues.get(Constants.Basic.CALCULATOR_ALL)));

        for (String budget : budgetValues.keySet()) {
            if (budget.equals(Constants.Basic.CALCULATOR_ALL)) {
                continue;
            }
            wrappers.add(getBudgetWrapper(budget, used, remaining, overUsed,
                    budgetValues.get(budget)));
        }
        mListWrappers.postValue(wrappers);
    }

    private BarWrapper getPaymentOrCategoryWrapper(String name,
            BigDecimal value,
            BigDecimal allValue) {
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            value = BigDecimal.ZERO;
        }
        int progress;
        if (allValue.compareTo(BigDecimal.ZERO) == 0) {
            progress = 100;
        } else {
            progress = CurrencyHelper.divide(value.multiply(HUNDRED), allValue).intValue();
        }
        return new BarWrapper(
                name,
                CurrencyHelper.formatForDisplay(true, value, true),
                progress);
    }

    private BarWrapper getBudgetWrapper(String name, String used,
            String remaining, String overUsed, Pair<BigDecimal, BigDecimal> valuePair) {
        if (valuePair == null) {
            valuePair = new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal budgetValue = valuePair.first;
        BigDecimal usedValue = valuePair.second;
        if (budgetValue == null) {
            budgetValue = BigDecimal.ZERO;
        }
        if (usedValue == null) {
            usedValue = BigDecimal.ZERO;
        }
        String value2;
        if (budgetValue.compareTo(usedValue) >= 0) {
            value2 = String.format(remaining,
                    CurrencyHelper.formatForDisplay(true, budgetValue.subtract(usedValue), true));
        } else {
            value2 = String.format(overUsed,
                    CurrencyHelper.formatForDisplay(true, usedValue.subtract(budgetValue), true));
        }
        int progress;
        if (budgetValue.compareTo(BigDecimal.ZERO) == 0) {
            progress = 100;
        } else {
            progress = CurrencyHelper.divide(usedValue.multiply(HUNDRED), budgetValue).intValue();
        }
        return new BarWrapper(
                name,
                String.format(used, CurrencyHelper.formatForDisplay(true, usedValue, true),
                        CurrencyHelper.formatForDisplay(true, budgetValue, true)),
                value2,
                progress);
    }

    public void onBudgetTabSelected(String allName, String used, String remaining,
            String overUsed) {
        prepareBudgetWrappers(allName, used, remaining, overUsed);
    }

    public void onPaymentsTabSelected(String allName) {
        preparePaymentWrappers(allName);
    }

    public void onCategoriesTabSelected(String allName) {
        prepareCategoryWrappers(allName);
    }
}

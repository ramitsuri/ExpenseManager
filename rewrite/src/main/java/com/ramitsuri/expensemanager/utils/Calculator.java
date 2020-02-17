package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import androidx.core.util.Pair;
import timber.log.Timber;

public class Calculator {
    private final CalculationLogger mLogger = new CalculationLogger();
    private final int mColumnWidth = 12;

    private List<Expense> mExpenses;
    private Map<String, BigDecimal> mPaymentMethodValueMap;
    private Map<String, BigDecimal> mCategoryValueMap;
    private Map<String, String> mCategoryBudgetMap;
    private Map<String, Pair<BigDecimal, BigDecimal>> mBudgetValueMap;
    private boolean mLog;

    public Calculator(@Nonnull List<Expense> expenses, @Nonnull List<Budget> budgets, boolean log) {
        mExpenses = expenses;
        mPaymentMethodValueMap = new HashMap<>();
        mCategoryValueMap = new HashMap<>();
        mCategoryBudgetMap = new HashMap<>();
        mBudgetValueMap = new HashMap<>();
        mLog = log;

        for (Budget budget : budgets) {
            // First - Budget amount, Second - Used amount
            mBudgetValueMap.put(budget.getName(), new Pair<>(budget.getAmount(), BigDecimal.ZERO));

            for (String category : budget.getCategories()) {
                mCategoryBudgetMap.put(category, budget.getName());
            }
        }
    }

    public void calculate() {
        BigDecimal budgetAllUsedValue = BigDecimal.ZERO;
        BigDecimal budgetAllValue = BigDecimal.ZERO;
        BigDecimal categoryAllValue = BigDecimal.ZERO;
        BigDecimal paymentMethodAllValue = BigDecimal.ZERO;

        for (Expense expense : mExpenses) {
            BigDecimal expenseValue = expense.getAmount();
            BigDecimal totalValue;

            // Payment method
            String paymentMethod = expense.getPaymentMethod();
            totalValue = mPaymentMethodValueMap.get(paymentMethod);
            if (totalValue == null) {
                totalValue = BigDecimal.ZERO;
            }
            totalValue = totalValue.add(expenseValue);
            paymentMethodAllValue = paymentMethodAllValue.add(expenseValue);
            mPaymentMethodValueMap.put(paymentMethod, totalValue);

            // Category
            String category = expense.getCategory();
            totalValue = mCategoryValueMap.get(category);
            if (totalValue == null) {
                totalValue = BigDecimal.ZERO;
            }
            totalValue = totalValue.add(expenseValue);
            categoryAllValue = categoryAllValue.add(expenseValue);
            mCategoryValueMap.put(category, totalValue);

            // Budget
            String budgetName = mCategoryBudgetMap.get(category);
            if (budgetName == null) {
                budgetName = Constants.Basic.EMPTY_BUDGET;
            }
            Pair<BigDecimal, BigDecimal> valuePair = mBudgetValueMap.get(budgetName);
            if (valuePair == null) {
                valuePair = new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            totalValue = valuePair.second; // This would not happen, there just for safety
            if (totalValue == null) {
                totalValue = BigDecimal.ZERO;
            }
            totalValue = totalValue.add(expenseValue);
            mBudgetValueMap.put(budgetName, new Pair<>(valuePair.first, totalValue));
        }
        for (String budget : mBudgetValueMap.keySet()) {
            Pair<BigDecimal, BigDecimal> valuePair = mBudgetValueMap.get(budget);
            if (valuePair != null && valuePair.first != null && valuePair.second != null) {
                budgetAllValue = budgetAllValue.add(valuePair.first);
                budgetAllUsedValue = budgetAllUsedValue.add(valuePair.second);
            }
        }

        mPaymentMethodValueMap.put(Constants.Basic.CALCULATOR_ALL, paymentMethodAllValue);
        mCategoryValueMap.put(Constants.Basic.CALCULATOR_ALL, categoryAllValue);
        mBudgetValueMap.put(Constants.Basic.CALCULATOR_ALL,
                new Pair<>(budgetAllValue, budgetAllUsedValue));

        if (!mLog) {
            return;
        }
        loggerConfigureCategories();
        for (String category : mCategoryValueMap.keySet()) {
            loggerAddCategory(category, mCategoryValueMap.get(category));
        }

        loggerConfigurePaymentMethods();
        for (String paymentMethod : mPaymentMethodValueMap.keySet()) {
            loggerAddPaymentMethod(paymentMethod, mPaymentMethodValueMap.get(paymentMethod));
        }

        loggerConfigureBudgets();
        for (String budget : mBudgetValueMap.keySet()) {
            Pair<BigDecimal, BigDecimal> valuePair = mBudgetValueMap.get(budget);
            if (valuePair != null) {
                if (valuePair.first != null && valuePair.second != null) {
                    loggerAddBudget(budget, valuePair.first, valuePair.second);
                }
            }
        }
        Timber.i(mLogger.getOutput() + "\n");
        mLogger.reset();
        Timber.i("Calculation done");
    }

    public Map<String, BigDecimal> getPaymentValues() {
        return mPaymentMethodValueMap;
    }

    public Map<String, BigDecimal> getCategoryValues() {
        return mCategoryValueMap;
    }

    public Map<String, Pair<BigDecimal, BigDecimal>> getBudgetValues() {
        return mBudgetValueMap;
    }

    private void loggerAddPaymentMethod(String name, BigDecimal value) {
        mLogger.addRow(
                name,
                CurrencyHelper.formatForDisplay(true, value));
    }

    private void loggerConfigurePaymentMethods() {
        mLogger.setColumns(new ArrayList<CalculationLogger.ColumnSpec>() {{
            add(new CalculationLogger.ColumnSpec("Name", "s", mColumnWidth));
            add(new CalculationLogger.ColumnSpec("Amount", "s", mColumnWidth));
        }});
        mLogger.addNewline();
        mLogger.addTitle("[[[     PAYMENT METHODS     ]]]");
        mLogger.addHeader();
    }

    private void loggerAddCategory(String name, BigDecimal value) {
        mLogger.addRow(
                name,
                CurrencyHelper.formatForDisplay(true, value));
    }

    private void loggerConfigureCategories() {
        mLogger.setColumns(new ArrayList<CalculationLogger.ColumnSpec>() {{
            add(new CalculationLogger.ColumnSpec("Name", "s", mColumnWidth));
            add(new CalculationLogger.ColumnSpec("Amount", "s", mColumnWidth));
        }});
        mLogger.addNewline();
        mLogger.addTitle("[[[     CATEGORIES     ]]]");
        mLogger.addHeader();
    }

    private void loggerAddBudget(String name, BigDecimal valueOriginal, BigDecimal valueRemain) {
        mLogger.addRow(
                name,
                CurrencyHelper.formatForDisplay(true, valueOriginal),
                CurrencyHelper.formatForDisplay(true, valueRemain));
    }

    private void loggerConfigureBudgets() {
        mLogger.setColumns(new ArrayList<CalculationLogger.ColumnSpec>() {{
            add(new CalculationLogger.ColumnSpec("Name", "s", mColumnWidth));
            add(new CalculationLogger.ColumnSpec("Budget", "s", mColumnWidth));
            add(new CalculationLogger.ColumnSpec("Used", "s", mColumnWidth));
        }});
        mLogger.addNewline();
        mLogger.addTitle("[[[     BUDGETS     ]]]");
        mLogger.addHeader();
    }
}

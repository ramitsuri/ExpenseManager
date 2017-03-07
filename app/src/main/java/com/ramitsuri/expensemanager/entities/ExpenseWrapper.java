package com.ramitsuri.expensemanager.entities;

import java.util.List;

public class ExpenseWrapper {

    private String mDate;
    private String mTopExpense;
    private String mTopCategory;
    private String mTopStore;
    private String mTotal;
    private List<Expense> mExpenses;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTopExpense() {
        return mTopExpense;
    }

    public void setTopExpense(String topExpense) {
        mTopExpense = topExpense;
    }

    public String getTopCategory() {
        return mTopCategory;
    }

    public void setTopCategory(String topCategory) {
        mTopCategory = topCategory;
    }

    public String getTopStore() {
        return mTopStore;
    }

    public void setTopStore(String topStore) {
        mTopStore = topStore;
    }

    public String getTotal() {
        return mTotal;
    }

    public void setTotal(String total) {
        mTotal = total;
    }

    public List<Expense> getExpenses() {
        return mExpenses;
    }

    public void setExpenses(List<Expense> expenses) {
        mExpenses = expenses;
    }
}

package com.ramitsuri.expensemanagerrewrite.entities;

import com.ramitsuri.expensemanagerrewrite.IntDefs.ListItemType;

import org.jetbrains.annotations.NotNull;

public class ExpenseWrapper {
    @ListItemType
    private int mItemType;
    private Expense mExpense;
    private String mDate;

    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int itemType) {
        mItemType = itemType;
    }

    public Expense getExpense() {
        return mExpense;
    }

    public void setExpense(Expense expense) {
        mExpense = expense;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    @NotNull
    @Override
    public String toString() {
        return "ExpenseWrapper{" +
                "mItemType=" + (mItemType == ListItemType.HEADER ? "Header" : "Item") +
                ", mExpense=" + mExpense +
                ", mDate='" + mDate +
                "}\n";
    }
}

package com.ramitsuri.expensemanager.ui.adapter;

import com.ramitsuri.expensemanager.constants.intDefs.ListItemType;
import com.ramitsuri.expensemanager.entities.Expense;

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

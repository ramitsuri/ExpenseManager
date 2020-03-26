package com.ramitsuri.expensemanager.ui.adapter;

import com.ramitsuri.expensemanager.entities.Budget;

public class BudgetSetupWrapper {

    private Budget mBudget;
    private boolean mIsSelected;

    public BudgetSetupWrapper(Budget budget) {
        mBudget = budget;
    }

    public Budget getBudget() {
        return mBudget;
    }

    public void setBudget(Budget budget) {
        mBudget = budget;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}

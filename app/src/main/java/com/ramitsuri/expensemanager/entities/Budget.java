package com.ramitsuri.expensemanager.entities;

import java.math.BigDecimal;
import java.util.List;

public class Budget {

    private int mId;
    private BigDecimal mTotalBudget;
    private List<Category> mCategories;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public BigDecimal getTotalBudget() {
        return mTotalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        mTotalBudget = totalBudget;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
    }
}

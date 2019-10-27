package com.ramitsuri.expensemanager.entities;

import java.math.BigDecimal;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "limit")
    private BigDecimal mLimit;

    @ColumnInfo(name = "used")
    private BigDecimal mUsed;


    private List<Category> mCategories;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public BigDecimal getLimit() {
        return mLimit;
    }

    public void setLimit(BigDecimal limit) {
        mLimit = limit;
    }

    public BigDecimal getUsed() {
        return mUsed;
    }

    public void setUsed(BigDecimal used) {
        mUsed = used;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
    }
}

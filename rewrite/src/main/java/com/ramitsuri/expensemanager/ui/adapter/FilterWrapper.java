package com.ramitsuri.expensemanager.ui.adapter;

import javax.annotation.Nonnull;

public class FilterWrapper {
    private String mValue;
    private boolean mIsSelected;

    public FilterWrapper(String value, boolean isSelected) {
        mValue = value;
        mIsSelected = isSelected;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Nonnull
    @Override
    public String toString() {
        return "\nFilterWrapper{" +
                "mValue='" + mValue + '\'' +
                ", mIsSelected=" + mIsSelected +
                "}";
    }
}

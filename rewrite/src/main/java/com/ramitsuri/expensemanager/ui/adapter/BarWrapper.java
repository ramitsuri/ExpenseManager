package com.ramitsuri.expensemanager.ui.adapter;

import javax.annotation.Nonnull;

public class BarWrapper {

    private String mTitle;
    private String mValueUsed;
    private String mValueRemaining;
    private int mProgress;

    public BarWrapper(String title, String valueUsed, String valueRemaining, int progress) {
        mTitle = title;
        mValueUsed = valueUsed;
        mValueRemaining = valueRemaining;
        mProgress = progress;
    }

    public BarWrapper(String title, String valueUsed, int progress) {
        mTitle = title;
        mValueUsed = valueUsed;
        mProgress = progress;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getValueUsed() {
        return mValueUsed;
    }

    public void setValueUsed(String valueUsed) {
        mValueUsed = valueUsed;
    }

    public String getValueRemaining() {
        return mValueRemaining;
    }

    public void setValueRemaining(String value2) {
        mValueRemaining = value2;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    @Nonnull
    @Override
    public String toString() {
        return "BarWrapper{" +
                "mTitle='" + mTitle + '\'' +
                ", mValueUsed='" + mValueUsed + '\'' +
                ", mValueRemaining='" + mValueRemaining + '\'' +
                ", mProgress=" + mProgress +
                "}";
    }
}

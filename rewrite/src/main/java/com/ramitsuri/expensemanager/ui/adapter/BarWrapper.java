package com.ramitsuri.expensemanager.ui.adapter;

import javax.annotation.Nonnull;

public class BarWrapper {

    private String mTitle;
    private String mValue1;
    private String mValue2;
    private String mValue3;
    private int mProgress;

    public BarWrapper(String title, String value1, String value2, String value3, int progress) {
        mTitle = title;
        mValue1 = value1;
        mValue2 = value2;
        mValue3 = value3;
        mProgress = progress;
    }

    public BarWrapper(String title, String value1, int progress) {
        mTitle = title;
        mValue1 = value1;
        mProgress = progress;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getValue1() {
        return mValue1;
    }

    public void setValue1(String value1) {
        mValue1 = value1;
    }

    public String getValue2() {
        return mValue2;
    }

    public void setValue2(String value2) {
        mValue2 = value2;
    }

    public String getValue3() {
        return mValue3;
    }

    public void setValue3(String value3) {
        mValue3 = value3;
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
                ", mValue1='" + mValue1 + '\'' +
                ", mValue2='" + mValue2 + '\'' +
                ", mValue3='" + mValue3 + '\'' +
                ", mProgress=" + mProgress +
                "}";
    }
}

package com.ramitsuri.expensemanager.ui.adapter;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;

import javax.annotation.Nonnull;

public class ListOptionsItemWrapper {

    @Nonnull
    private String mValue;
    @RecordType
    private String mRecordType;

    public ListOptionsItemWrapper(@Nonnull String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public ListOptionsItemWrapper setValue(String value) {
        mValue = value;
        return this;
    }

    public String getRecordType() {
        return mRecordType;
    }

    public ListOptionsItemWrapper setRecordType(String recordType) {
        mRecordType = recordType;
        return this;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ListOptionsItemWrapper{" +
                "mValue='" + mValue + '\'' +
                ", mRecordType='" + mRecordType + '\'' +
                '}';
    }
}

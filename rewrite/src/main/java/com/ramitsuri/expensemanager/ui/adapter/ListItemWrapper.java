package com.ramitsuri.expensemanager.ui.adapter;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;

import javax.annotation.Nonnull;

public class ListItemWrapper implements ListEqualizer{

    @Nonnull
    private String mValue;
    @RecordType
    private String mRecordType;

    public ListItemWrapper(@Nonnull String value) {
        mValue = value;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    public ListItemWrapper setValue(String value) {
        mValue = value;
        return this;
    }

    @RecordType
    public String getRecordType() {
        return mRecordType;
    }

    public ListItemWrapper setRecordType(String recordType) {
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

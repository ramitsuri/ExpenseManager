package com.ramitsuri.expensemanager.entities;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.util.Date;

import javax.annotation.Nonnull;

public class Filter {
    private long mFromDateTime;
    private long mToDateTime;

    public long getFromDateTime() {
        return mFromDateTime;
    }

    public Filter setFromDateTime(long fromDateTime) {
        mFromDateTime = fromDateTime;
        return this;
    }

    public long getToDateTime() {
        return mToDateTime;
    }

    public Filter setToDateTime(long toDateTime) {
        mToDateTime = toDateTime;
        return this;
    }

    @Override
    @Nonnull
    public String toString() {
        return "Filter{" +
                "mFromDateTime=" + new Date(mFromDateTime) +
                ", mToDateTime=" + new Date(mToDateTime) +
                '}';
    }

    public String toFriendlyString() {
        return MainApplication.getInstance().getResources()
                .getString(R.string.expenses_filter_format,
                        DateHelper.getFriendlyDate(mFromDateTime),
                        DateHelper.getFriendlyDate(mToDateTime));
    }
}

package com.ramitsuri.expensemanager.entities;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;

public class Filter {
    private Calendar mCalendar;

    private int mMonthIndex;
    private long mFromDateTime;
    private long mToDateTime;

    public Filter() {
        mCalendar = Calendar.getInstance();
        setMonthIndex(Constants.Basic.UNDEFINED);
    }

    public Filter setMonthIndex(int monthIndex) {
        mMonthIndex = monthIndex;
        onMonthIndexSet(monthIndex);
        return this;
    }

    public long getFromDateTime() {
        return mFromDateTime;
    }

    public long getToDateTime() {
        return mToDateTime;
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

    private void onMonthIndexSet(int monthIndex) {
        if (mMonthIndex != Constants.Basic.UNDEFINED) {
            mCalendar.set(Calendar.MONTH, monthIndex);
        }

        // First day of month - 00:00:00 001ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 1);
        mFromDateTime = mCalendar.getTimeInMillis();

        // Last Day of month - 23:59:59 999ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        mCalendar.set(Calendar.MILLISECOND, 999);
        mToDateTime = mCalendar.getTimeInMillis();
    }
}

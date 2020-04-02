package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.entities.Filter;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;

public class FilterOptionsViewModel extends ViewModel {

    private Filter mFilter;
    private Calendar mCalendar;

    public FilterOptionsViewModel() {
        super();
        mFilter = new Filter();
        mCalendar = Calendar.getInstance();
    }

    public Filter onMonthPicked(int monthIndex) {
        mCalendar.set(Calendar.MONTH, monthIndex);

        // First day of month - 00:00:00 001ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 1);
        long fromDateTime = mCalendar.getTimeInMillis();

        // Last Day of month - 23:59:59 999ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        mCalendar.set(Calendar.MILLISECOND, 999);
        long toDateTime = mCalendar.getTimeInMillis();

        return mFilter
                .setFromDateTime(fromDateTime)
                .setToDateTime(toDateTime);
    }
}

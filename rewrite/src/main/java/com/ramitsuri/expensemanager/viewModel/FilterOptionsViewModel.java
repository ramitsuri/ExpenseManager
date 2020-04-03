package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.entities.Filter;

import androidx.lifecycle.ViewModel;

public class FilterOptionsViewModel extends ViewModel {

    private Filter mFilter;

    public FilterOptionsViewModel() {
        super();
        mFilter = new Filter();
    }

    public Filter onMonthPicked(int monthIndex) {
        return mFilter
                .setMonthIndex(monthIndex);
    }
}

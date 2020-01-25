package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MiscellaneousViewModel extends ViewModel {

    private long mLastPressTime;

    public MiscellaneousViewModel() {
        super();
        mLastPressTime = 0;
    }

    public void initiateBackup() {
        Timber.i("Initiating backup");
        WorkHelper.enqueueOneTimeBackup();
    }

    public void syncDataFromSheet() {
        Timber.i("Initiating sync");
        WorkHelper.enqueueOneTimeSync();
    }

    public void deleteExpenses() {
        Timber.i("Deleting expenses");
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastPressTime <= 2000) {
            if (BuildConfig.DEBUG) { // Extra protection, only in debug
                MainApplication.getInstance().getExpenseRepo().deleteExpenses();
            }
            mLastPressTime = 0;
        } else {
            mLastPressTime = currentTime;
        }
    }

    public boolean enableHidden() {
        return AppHelper.isDebugOptionEnabled() || BuildConfig.DEBUG;
    }

    public boolean enableDeleteAll() {
        return BuildConfig.DEBUG;
    }
}

package com.ramitsuri.expensemanager.utils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class CrashReportingHelper {
    private FirebaseCrashlytics mCrashReporter;

    private CrashReportingHelper() {
        if (mCrashReporter == null) {
            mCrashReporter = FirebaseCrashlytics.getInstance();
        }
    }

    public static CrashReportingHelper getInstance() {
        return new CrashReportingHelper();
    }

    public void log(String message) {
        mCrashReporter.log(message);
    }

    public void log(Throwable throwable) {
        mCrashReporter.recordException(throwable);
    }

    public void log(String message, Throwable throwable) {
        log(message);
        log(throwable);
    }
}

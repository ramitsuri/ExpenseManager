package com.ramitsuri.expensemanager.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.work.RecurringExpensesWorker;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WorkHelper {

    /**
     * Periodic Recurring Expenses runner
     */
    public static void enqueueRecurringExpensesRunner() {
        Timber.i("Enqueue recurring expenses runner invoked");
        // Prepare
        String tag = getRecurringExpensesRunnerTag();
        enqueuePeriodicWork(tag, RecurringExpensesWorker.class, false, false, 24);
    }

    public static LiveData<List<WorkInfo>> getWorkStatus(String tag) {
        return getInstance()
                .getWorkInfosByTagLiveData(tag);
    }

    public static String getRecurringExpensesRunnerTag() {
        return Constants.Tag.RECURRING_EXPENSES_RUNNER;
    }

    public static void pruneWork() {
        getInstance().pruneWork();
    }

    public static void cancelByTag(@NonNull String... tags) {
        for (String tag : tags) {
            getInstance()
                    .cancelAllWorkByTag(tag);
        }
    }

    private static Constraints getConstraints() {
        return new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();
    }

    private static void enqueueOneTimeWork(String tag,
            @NonNull Class<? extends ListenableWorker> workerClass, boolean scheduled) {
        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request Builder
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest
                .Builder(workerClass)
                .setInputData(input)
                .addTag(tag);

        if (scheduled) {
            builder
                    .setInitialDelay(DateHelper.getDelayForPeriodicWork(Calendar.getInstance(), 1));
        }
        OneTimeWorkRequest request = builder.build();

        // Enqueue
        getInstance().enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, request);
    }

    private static void enqueuePeriodicWork(String tag,
            @NonNull Class<? extends ListenableWorker> workerClass,
            boolean setConstraints,
            boolean replace,
            long repeatHours) {
        Constraints constraints = getConstraints();
        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest
                .Builder(workerClass, repeatHours, TimeUnit.HOURS)
                .setInputData(input)
                .addTag(tag)
                .setInitialDelay(DateHelper.getDelayForPeriodicWork(Calendar.getInstance(), 3));
        if (setConstraints) {
            builder.setConstraints(constraints);
        }
        ExistingPeriodicWorkPolicy policy = ExistingPeriodicWorkPolicy.KEEP;
        if (replace) {
            policy = ExistingPeriodicWorkPolicy.REPLACE;
        }
        // Enqueue
        getInstance().enqueueUniquePeriodicWork(tag, policy, builder.build());
    }

    private static WorkManager getInstance() {
        return WorkManager.getInstance(MainApplication.getInstance());
    }
}

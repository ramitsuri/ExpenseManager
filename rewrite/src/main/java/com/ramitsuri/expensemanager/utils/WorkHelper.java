package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.work.BackupWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import timber.log.Timber;

public class WorkHelper {

    public static void enqueueOneTimeBackup() {
        Timber.i("Enqueue one-time backup invoked");
        String tag = getOneTimeWorkTag();

        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, Constants.LogType.ONE_TIME_BACKUP)
                .build();

        // Request
        OneTimeWorkRequest backupRequest = new OneTimeWorkRequest
                .Builder(BackupWorker.class)
                .setInputData(input)
                .addTag(tag)
                .build();

        // Enqueue
        getInstance()
                .enqueue(backupRequest);
    }

    /**
     * Will run every 12 hours
     */
    public static void enqueuePeriodicBackup() {
        Timber.i("Enqueue scheduled backup invoked");
        // Prepare
        String tag = getPeriodicWorkTag();
        Constraints constraints = getConstraints();
        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, Constants.LogType.PERIODIC_BACKUP)
                .build();

        // Request
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(BackupWorker.class, 1, TimeUnit.DAYS)
                .setInputData(input)
                .addTag(tag)
                .setConstraints(constraints)
                .build();

        // Enqueue
        getInstance()
                .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.REPLACE, request);
    }

    public static void cancelScheduledBackup() {
        Timber.i("Cancel scheduled backup invoked");
        String tag = getPeriodicWorkTag();
        getInstance().cancelAllWorkByTag(tag);
    }

    public static String getOneTimeWorkTag() {
        return Constants.Tag.ONE_TIME_BACKUP;
    }

    public static String getPeriodicWorkTag() {
        return Constants.Tag.SCHEDULED_BACKUP;
    }

    public static LiveData<List<WorkInfo>> getWorkStatus(String tag) {
        return getInstance()
                .getWorkInfosByTagLiveData(tag);
    }

    private static Constraints getConstraints() {
        return new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();
    }

    private static Data getInputData(String appName, String accountName, String accountType,
            String spreadsheetId, String sheetId) {
        Data.Builder builder = new Data.Builder();
        builder.putString(Constants.Work.APP_NAME, appName);
        builder.putString(Constants.Work.ACCOUNT_NAME, accountName);
        builder.putString(Constants.Work.ACCOUNT_TYPE, accountType);
        builder.putString(Constants.Work.SPREADSHEET_ID, spreadsheetId);
        builder.putString(Constants.Work.SHEET_ID, sheetId);
        return builder.build();
    }

    private static WorkManager getInstance() {
        return WorkManager.getInstance(MainApplication.getInstance());
    }
}

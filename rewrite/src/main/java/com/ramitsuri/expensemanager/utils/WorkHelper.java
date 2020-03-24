package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.work.BackupWorker;
import com.ramitsuri.expensemanager.work.ExpenseSyncWorker;
import com.ramitsuri.expensemanager.work.SyncWorker;

import java.util.Calendar;
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

    /**
     * One Time Backup
     */
    public static void enqueueOneTimeBackup() {
        Timber.i("Enqueue one-time backup invoked");
        String tag = getOneTimeWorkTag();

        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
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
     * Periodic Backup
     * Runs once a day around 2AM
     */
    public static void enqueuePeriodicBackup() {
        Timber.i("Enqueue scheduled backup invoked");
        // Prepare
        String tag = getPeriodicWorkTag();
        Constraints constraints = getConstraints();
        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(BackupWorker.class, 1, TimeUnit.DAYS)
                .setInputData(input)
                .addTag(tag)
                .setInitialDelay(DateHelper.getDelayForPeriodicWork(Calendar.getInstance(), 2))
                .setConstraints(constraints)
                .build();

        // Enqueue
        getInstance()
                .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.REPLACE, request);
    }

    public static void cancelPeriodicLegacyBackup() {
        Timber.i("Cancel scheduled backup invoked");
        String tag = getPeriodicWorkLegacyTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    public static void cancelPeriodicBackup() {
        Timber.i("Cancel scheduled backup invoked");
        String tag = getPeriodicWorkTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    /**
     * One Time Entities Sync
     */
    public static void enqueueOneTimeSync() {
        Timber.i("Enqueue one-time sync invoked");
        String tag = getOneTimeSyncTag();

        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest
                .Builder(SyncWorker.class)
                .setInputData(input)
                .addTag(tag)
                .build();

        // Enqueue
        getInstance()
                .enqueue(syncRequest);
    }

    /**
     * One Time Expenses Sync
     */
    public static void enqueueOneTimeExpenseSync() {
        Timber.i("Enqueue one-time expenses sync invoked");
        String tag = getOneTimeExpenseSyncTag();

        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest
                .Builder(ExpenseSyncWorker.class)
                .setInputData(input)
                .addTag(tag)
                .build();

        // Enqueue
        getInstance()
                .enqueue(syncRequest);
    }

    public static LiveData<List<WorkInfo>> getWorkStatus(String tag) {
        return getInstance()
                .getWorkInfosByTagLiveData(tag);
    }

    public static String getPeriodicWorkLegacyTag() {
        return Constants.Tag.SCHEDULED_BACKUP_LEGACY;
    }

    public static String getPeriodicWorkTag() {
        return Constants.Tag.PERIODIC_BACKUP;
    }

    private static String getOneTimeWorkTag() {
        return Constants.Tag.ONE_TIME_BACKUP;
    }

    private static String getOneTimeSyncTag() {
        return Constants.Tag.ONE_TIME_SYNC;
    }

    private static String getOneTimeExpenseSyncTag() {
        return Constants.Tag.ONE_TIME_EXPENSE_SYNC;
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

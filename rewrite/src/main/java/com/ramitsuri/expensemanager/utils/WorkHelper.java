package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.work.ExpensesBackupWorker;
import com.ramitsuri.expensemanager.work.CreateSpreadsheetWorker;
import com.ramitsuri.expensemanager.work.EntitiesBackupWorker;
import com.ramitsuri.expensemanager.work.ExpenseSyncWorker;
import com.ramitsuri.expensemanager.work.SyncWorker;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import timber.log.Timber;

public class WorkHelper {

    /**
     * One Time Backup
     */
    public static void enqueueOneTimeBackup() {
        Timber.i("Enqueue one-time backup invoked");

        String tag = getOneTimeWorkTag();
        enqueueOneTimeWork(tag, ExpensesBackupWorker.class);
    }

    public static void cancelOneTimeBackup() {
        Timber.i("Cancel one time backup invoked");

        String tag = getOneTimeWorkTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    public static void cancelPeriodicLegacyBackup() {
        Timber.i("Cancel scheduled backup invoked");

        String tag = getPeriodicWorkLegacyTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    /**
     * Periodic Backup
     * Runs once every 2 days around 3AM
     */
    public static void enqueuePeriodicBackup(boolean replace) {
        Timber.i("Enqueue scheduled backup invoked");

        String tag = getPeriodicExpensesBackupTag();
        enqueuePeriodicWork(tag, ExpensesBackupWorker.class, false, replace);
    }

    public static void cancelPeriodicBackup() {
        Timber.i("Cancel scheduled backup invoked");

        String tag = getPeriodicExpensesBackupTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    /**
     * One Time Entities Sync
     */
    public static void enqueueOneTimeSync() {
        Timber.i("Enqueue one-time sync invoked");

        String tag = getOneTimeSyncTag();
        enqueueOneTimeWork(tag, SyncWorker.class);
    }

    /**
     * One Time Expenses Sync
     */
    public static void enqueueOneTimeExpenseSync() {
        Timber.i("Enqueue one-time expenses sync invoked");

        String tag = getOneTimeExpenseSyncTag();
        enqueueOneTimeWork(tag, ExpenseSyncWorker.class);
    }

    /**
     * One Time Entities Backup
     */
    public static void enqueueOneTimeEntitiesBackup(boolean scheduled) {
        Timber.i("Enqueue one-time entities backup invoked");

        String tag = getOneTimeEntitiesBackupTag();
        enqueueOneTimeWork(tag, EntitiesBackupWorker.class, scheduled);
    }

    public static void cancelOneTimeEntitiesBackup() {
        Timber.i("Cancel one time entities backup invoked");

        String tag = getOneTimeEntitiesBackupTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    /**
     * Periodic Entities Backup
     */
    public static void enqueuePeriodicEntitiesBackup() {
        Timber.i("Enqueue periodic entities backup invoked");
        // Prepare
        String tag = getPeriodicEntitiesBackupTag();
        enqueuePeriodicWork(tag, EntitiesBackupWorker.class, true, false);
    }

    public static void cancelPeriodicEntitiesBackup() {
        Timber.i("Cancel scheduled entities backup invoked");

        String tag = getPeriodicEntitiesBackupTag();
        getInstance()
                .cancelAllWorkByTag(tag);
    }

    /**
     * One time create spreadsheet
     */
    public static void enqueueOneTimeCreateSpreadsheet() {
        Timber.i("Enqueue one-time spreadsheet creation invoked");

        String tag = getOneTimeCreateSpreadsheetTag();
        enqueueOneTimeWork(tag, CreateSpreadsheetWorker.class);
    }

    public static LiveData<List<WorkInfo>> getWorkStatus(String tag) {
        return getInstance()
                .getWorkInfosByTagLiveData(tag);
    }

    public static String getPeriodicWorkLegacyTag() {
        return Constants.Tag.PERIODIC_BACKUP_LEGACY;
    }

    public static String getPeriodicExpensesBackupTag() {
        return Constants.Tag.PERIODIC_EXPENSES_BACKUP;
    }

    private static String getPeriodicEntitiesBackupTag() {
        return Constants.Tag.PERIODIC_ENTITIES_BACKUP;
    }

    public static String getOneTimeWorkTag() {
        return Constants.Tag.ONE_TIME_BACKUP;
    }

    private static String getOneTimeEntitiesBackupTag() {
        return Constants.Tag.ONE_TIME_ENTITIES_BACKUP;
    }

    private static String getOneTimeSyncTag() {
        return Constants.Tag.ONE_TIME_SYNC;
    }

    private static String getOneTimeExpenseSyncTag() {
        return Constants.Tag.ONE_TIME_EXPENSE_SYNC;
    }

    public static String getOneTimeCreateSpreadsheetTag() {
        return Constants.Tag.ONE_TIME_CREATE_SPREADSHEET;
    }

    public static void pruneWork() {
        getInstance().pruneWork();
    }

    private static Constraints getConstraints() {
        return new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();
    }

    private static void enqueueOneTimeWork(String tag,
            @NonNull Class<? extends ListenableWorker> workerClass) {
        enqueueOneTimeWork(tag, workerClass, false);
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
            boolean replace) {
        Constraints constraints = getConstraints();
        Data input = new Data.Builder()
                .putString(Constants.Work.TYPE, tag)
                .build();

        // Request
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest
                .Builder(workerClass, 48, TimeUnit.HOURS)
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

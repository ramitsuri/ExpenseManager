package com.ramitsuri.expensemanager.work;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class BackupWorker extends Worker {

    public BackupWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String appName = getInputData().getString(Constants.Work.APP_NAME);
        String spreadsheetId = getInputData().getString(Constants.Work.SPREADSHEET_ID);
        String accountName = getInputData().getString(Constants.Work.ACCOUNT_NAME);
        String accountType = getInputData().getString(Constants.Work.ACCOUNT_TYPE);
        String sheetId = getInputData().getString(Constants.Work.SHEET_ID);

        if (appName == null || TextUtils.isEmpty(spreadsheetId) || TextUtils.isEmpty(sheetId) ||
                TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i(
                    "App Name - %s / Spreadsheet Id - %s / Sheet Id - %s / Account Name - %s / Account Type - %s null or empty",
                    appName, spreadsheetId, sheetId,
                    accountName, accountType);
            return Result.failure();
        }

        Account account = new Account(accountName, accountType);

        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.i("Sheet repo null");
            MainApplication.getInstance()
                    .initSheetRepo(account, spreadsheetId, Arrays.asList(Constants.SCOPES));
        }

        /*List<TrackedItem> trackedItemsToBackup =
                CalorieTrackerDatabase.getInstance().trackedItemDao().getAllUnsynced();

        List<Item> items = CalorieTrackerDatabase.getInstance().itemDao().getAllNonLive();

        if (trackedItemsToBackup == null) {
            Timber.i("Questions to backup is null");
            return Result.failure();
        }

        InsertConsumerResponse response = MainApplication.getInstance().getSheetRepository()
                .getInsertRangeResponse(trackedItemsToBackup, items, sheetId);
        if (response.isSuccessful()) {
            MainApplication.getInstance().getTrackedItemRepository().deleteAll();
            return Result.success();
        }*/
        return Result.failure();
    }
}

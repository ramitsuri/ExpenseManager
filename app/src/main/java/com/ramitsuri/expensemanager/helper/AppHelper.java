package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.constants.PrefKeys;

public class AppHelper {

    public static boolean isFirstRunComplete() {
        return PrefHelper.get(PrefKeys.IS_FIRST_RUN_COMPLETE, false);
    }

    public static void setFirstRunComplete() {
        PrefHelper.set(PrefKeys.IS_FIRST_RUN_COMPLETE, true);
    }

    public static long getLastAddedID() {
        return PrefHelper.get(PrefKeys.LAST_STORED_ID, 20170101000L);
    }

    public static void setLastAddedID(long value) {
        PrefHelper.set(PrefKeys.LAST_STORED_ID, value);
    }

    public static String getCurrency() {
        return PrefHelper.get(PrefKeys.CURRENCY, Others.DEFAULT_CURRENCY);
    }

    public static void setCurrency(String currency) {
        PrefHelper.set(PrefKeys.CURRENCY, currency);
    }

    public static String getAccountName() {
        return PrefHelper.get(PrefKeys.ACCOUNT_NAME, null);
    }

    public static void setAccountName(String name) {
        PrefHelper.set(PrefKeys.ACCOUNT_NAME, name);
    }

    public static void setSpreadsheetId(String id) {
        PrefHelper.set(PrefKeys.SPREADSHEET_ID, id);
    }

    public static String getSpreadsheetId() {
        return PrefHelper.get(MainApplication.getInstance()
                .getString(R.string.preference_key_spreadsheet_id), null);
    }

    public static void setSheetsId(String id) {
        PrefHelper.set(PrefKeys.SHEETS_ID, id);
    }

    public static String getSheetsId() {
        return PrefHelper
                .get(MainApplication.getInstance().getString(R.string.preference_key_sheets_id),
                        null);
    }

    public static void setLastBackupTime(long time) {
        PrefHelper.set(PrefKeys.LAST_BACKUP_TIME, time);
    }

    public static long getLastBackupTimeInMillis() {
        return PrefHelper.get(PrefKeys.LAST_BACKUP_TIME, 0L);
    }

    public static void setFirstBackupComplete(boolean isFirstBackupDone) {
        PrefHelper.set(PrefKeys.IS_FIRST_BACKUP_DONE, isFirstBackupDone);
    }

    public static boolean isFirstBackupComplete() {
        return PrefHelper.get(PrefKeys.IS_FIRST_BACKUP_DONE, false);
    }

    public static void setBackupWorkerEnqueued(boolean isBackupWorkerEnqueued) {
        PrefHelper.set(PrefKeys.IS_BACKUP_WORKER_ENQUEQUED, isBackupWorkerEnqueued);
    }

    public static boolean isBackupWorkerEnqueued() {
        return PrefHelper.get(PrefKeys.IS_BACKUP_WORKER_ENQUEQUED, false);
    }
}

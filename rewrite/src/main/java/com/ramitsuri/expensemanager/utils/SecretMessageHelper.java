package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.constants.stringDefs.PrefKeys;
import com.ramitsuri.expensemanager.constants.stringDefs.SecretMessages;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SecretMessageHelper {

    public static void onSecretMessageEntered(@Nonnull String message) {
        message = message.trim().toUpperCase();
        boolean handled = false;
        switch (message) {
            case SecretMessages.ENABLE_SPLITTING:
                Timber.i("Enabling splitting");
                setSplittingEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_SPLITTING:
                Timber.i("Disabling splitting");
                setSplittingEnabled(false);
                handled = true;
                break;

            case SecretMessages.ENABLE_EXPENSE_SYNC:
                Timber.i("Enabling expense sync");
                setExpenseSyncEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_EXPENSE_SYNC:
                Timber.i("Disabling expense sync");
                setExpenseSyncEnabled(false);
                handled = true;
                break;

            case SecretMessages.ENABLE_ENTITIES_SYNC:
                Timber.i("Enabling entities sync");
                setEntitiesSyncEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_ENTITIES_SYNC:
                Timber.i("Disabling entities sync");
                setEntitiesSyncEnabled(false);
                handled = true;
                break;

            case SecretMessages.ENABLE_INCOME:
                Timber.i("Enabling income");
                setIncomeEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_INCOME:
                Timber.i("Disabling income");
                setIncomeEnabled(false);
                handled = true;
                break;

            case SecretMessages.ENABLE_WORK_LOG:
                Timber.i("Enabling work log");
                setWorkLogEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_WORK_LOG:
                Timber.i("Disabling work log");
                setWorkLogEnabled(false);
                handled = true;
                break;

            case SecretMessages.CANCEL_ONE_TIME:
                Timber.i("Cancel all one time work");
                WorkHelper.cancelOneTimeBackup();
                WorkHelper.cancelOneTimeEntitiesBackup();
                handled = true;
                break;

            case SecretMessages.CANCEL_PERIODIC:
                Timber.i("Cancel all periodic work");
                WorkHelper.cancelPeriodicBackup();
                WorkHelper.cancelPeriodicEntitiesBackup();
                handled = true;
                break;

            case SecretMessages.ENQUEUE_ONE_TIME:
                Timber.i("Enqueue all one time work");
                WorkHelper.enqueueOneTimeBackup();
                WorkHelper.enqueueOneTimeEntitiesBackup(false);
                handled = true;
                break;

            case SecretMessages.ENQUEUE_PERIODIC:
                Timber.i("Enqueue all periodic work");
                WorkHelper.enqueuePeriodicBackup();
                WorkHelper.enqueuePeriodicEntitiesBackup();
                handled = true;
                break;

            case SecretMessages.ENABLE_BACKUP_NOW:
                Timber.i("Enabling backup now");
                setBackupNowEnabled(true);
                handled = true;
                break;

            case SecretMessages.DISABLE_BACKUP_NOW:
                Timber.i("Disabling backup now");
                setBackupNowEnabled(false);
                handled = true;
                break;

            default:
                Timber.i("Secret message means nothing");
        }

        if (!handled) {
            if (message.startsWith(SecretMessages.SET_SPREADSHEET_ID)) {
                String[] parts = message.split(" ");
                if (parts.length >= 2) {
                    String spreadsheetId = parts[1];
                    Timber.i("Set spreadsheet id %s", spreadsheetId);
                    AppHelper.setSpreadsheetId(spreadsheetId);
                } else {
                    AppHelper.setSpreadsheetId("");
                }
            }
        }
    }

    public static boolean isSplittingEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_SPLITTING, false);
    }

    private static void setSplittingEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_SPLITTING, enable);
    }

    public static boolean isExpenseSyncEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_EXPENSE_SYNC, false);
    }

    private static void setExpenseSyncEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_EXPENSE_SYNC, enable);
    }

    public static boolean isEntitiesSyncEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_ENTITIES_SYNC, false);
    }

    private static void setEntitiesSyncEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_ENTITIES_SYNC, enable);
    }

    public static boolean isIncomeEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_INCOME, false);
    }

    private static void setIncomeEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_INCOME, enable);
    }

    public static boolean isWorkLogEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_WORK_LOG, false);
    }

    private static void setWorkLogEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_WORK_LOG, enable);
    }

    public static boolean isBackupNowEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_BACKUP_NOW, false);
    }

    private static void setBackupNowEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_BACKUP_NOW, enable);
    }
}

package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.constants.stringDefs.PrefKeys;
import com.ramitsuri.expensemanager.constants.stringDefs.SecretMessages;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SecretMessageHelper {

    public static void onSecretMessageEntered(@Nonnull String message) {
        switch (message.trim().toUpperCase()) {
            case SecretMessages.ENABLE_SPLITTING:
                Timber.i("Enabling splitting");
                setSplittingEnabled(true);
                break;

            case SecretMessages.DISABLE_SPLITTING:
                Timber.i("Disabling splitting");
                setSplittingEnabled(false);
                break;

            case SecretMessages.ENABLE_EXPENSE_SYNC:
                Timber.i("Enabling expense sync");
                setExpenseSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_EXPENSE_SYNC:
                Timber.i("Disabling expense sync");
                setExpenseSyncEnabled(false);
                break;

            case SecretMessages.ENABLE_ENTITIES_SYNC:
                Timber.i("Enabling entities sync");
                setEntitiesSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_ENTITIES_SYNC:
                Timber.i("Disabling entities sync");
                setEntitiesSyncEnabled(false);
                break;

            case SecretMessages.ENABLE_INCOME:
                Timber.i("Enabling income");
                setIncomeEnabled(true);
                break;

            case SecretMessages.DISABLE_INCOME:
                Timber.i("Disabling income");
                setIncomeEnabled(false);
                break;

            case SecretMessages.ENABLE_WORK_LOG:
                Timber.i("Enabling work log");
                setWorkLogEnabled(true);
                break;

            case SecretMessages.DISABLE_WORK_LOG:
                Timber.i("Disabling work log");
                setWorkLogEnabled(false);
                break;

            case SecretMessages.CANCEL_ONE_TIME:
                Timber.i("Cancel all one time work");
                WorkHelper.cancelOneTimeBackup();
                WorkHelper.cancelOneTimeEntitiesBackup();
                break;

            case SecretMessages.CANCEL_PERIODIC:
                Timber.i("Cancel all periodic work");
                WorkHelper.cancelPeriodicBackup();
                WorkHelper.cancelPeriodicEntitiesBackup();
                break;

            case SecretMessages.ENQUEUE_ONE_TIME:
                Timber.i("Enqueue all one time work");
                WorkHelper.enqueueOneTimeBackup();
                WorkHelper.enqueueOneTimeEntitiesBackup();
                break;

            case SecretMessages.ENQUEUE_PERIODIC:
                Timber.i("Enqueue all periodic work");
                WorkHelper.enqueuePeriodicBackup();
                WorkHelper.enqueuePeriodicEntitiesBackup();
                break;

            case SecretMessages.ENABLE_BACKUP_NOW:
                Timber.i("Enabling backup now");
                setBackupNowEnabled(true);
                break;

            case SecretMessages.DISABLE_BACKUP_NOW:
                Timber.i("Disabling backup now");
                setBackupNowEnabled(false);
                break;

            default:
                Timber.i("Secret message means nothing");
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

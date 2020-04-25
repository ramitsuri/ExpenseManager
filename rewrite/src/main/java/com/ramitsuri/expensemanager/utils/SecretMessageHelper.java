package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.constants.stringDefs.SecretMessages;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SecretMessageHelper {

    public static void onSecretMessageEntered(@Nonnull String message) {
        switch (message.trim().toUpperCase()) {
            case SecretMessages.ENABLE_SPLITTING:
                Timber.i("Enabling splitting");
                AppHelper.setSplittingEnabled(true);
                break;

            case SecretMessages.DISABLE_SPLITTING:
                Timber.i("Disabling splitting");
                AppHelper.setSplittingEnabled(false);
                break;

            case SecretMessages.ENABLE_EXPENSE_SYNC:
                Timber.i("Enabling expense sync");
                AppHelper.setExpenseSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_EXPENSE_SYNC:
                Timber.i("Disabling expense sync");
                AppHelper.setExpenseSyncEnabled(false);
                break;

            case SecretMessages.ENABLE_ENTITIES_SYNC:
                Timber.i("Enabling entities sync");
                AppHelper.setEntitiesSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_ENTITIES_SYNC:
                Timber.i("Disabling entities sync");
                AppHelper.setEntitiesSyncEnabled(false);
                break;

            case SecretMessages.ENABLE_INCOME:
                Timber.i("Enabling income");
                AppHelper.setIncomeEnabled(true);
                break;

            case SecretMessages.DISABLE_INCOME:
                Timber.i("Disabling income");
                AppHelper.setIncomeEnabled(false);
                break;

            case SecretMessages.ENABLE_WORK_LOG:
                Timber.i("Enabling work log");
                AppHelper.setWorkLogEnabled(true);
                break;

            case SecretMessages.DISABLE_WORK_LOG:
                Timber.i("Disabling work log");
                AppHelper.setWorkLogEnabled(false);
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

            default:
                Timber.i("Secret message means nothing");
        }
    }
}

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
                handled = true;
                break;

            case SecretMessages.CANCEL_PERIODIC:
                Timber.i("Cancel all periodic work");
                handled = true;
                break;
        }

        if (!handled) {
            Timber.i("Secret message means nothing");
        }
    }

    public static boolean isWorkLogEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_WORK_LOG, false);
    }

    private static void setWorkLogEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_WORK_LOG, enable);
    }
}

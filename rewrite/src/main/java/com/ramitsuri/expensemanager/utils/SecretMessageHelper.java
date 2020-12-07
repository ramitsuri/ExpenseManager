package com.ramitsuri.expensemanager.utils;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.stringDefs.PrefKeys;
import com.ramitsuri.expensemanager.constants.stringDefs.SecretMessages;
import com.ramitsuri.expensemanager.entities.EditedSheet;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SecretMessageHelper {

    public static void onSecretMessageEntered(@Nonnull String message) {
        message = message.trim().toUpperCase();
        boolean handled = false;
        switch (message) {
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
                WorkHelper.enqueuePeriodicBackup(true);
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

            case SecretMessages.DELETE_EDITED_MONTHS:
                Timber.i("Deleting edited months");
                MainApplication.getInstance().getEditedSheetRepo().deleteAll();
                handled = true;
                break;
        }

        if (!handled) {
            // set_spreadsheet_id <id> or empty for remove spreadsheet id
            if (message.startsWith(SecretMessages.SET_SPREADSHEET_ID)) { // Spreadsheet ID
                String[] parts = message.split(" ");
                if (parts.length >= 2) {
                    String spreadsheetId = parts[1];
                    Timber.i("Set spreadsheet id %s", spreadsheetId);
                    AppHelper.setSpreadsheetId(spreadsheetId);
                } else {
                    AppHelper.setSpreadsheetId("");
                }
                handled = true;
            }
            // force_month_sync <0-11>
            else if (message.startsWith(SecretMessages.FORCE_MONTH_SYNC)) { // Force month sync
                String[] parts = message.split(" ");
                if (parts.length == 2) {
                    try {
                        int monthIndex = Integer.parseInt(parts[1]);
                        if (monthIndex >= 0 && monthIndex < 12) {
                            MainApplication.getInstance().getEditedSheetRepo()
                                    .insertEditedSheet(new EditedSheet(monthIndex));
                            handled = true;
                        }
                    } catch (NumberFormatException e) {
                        Timber.e("Invalid month");
                    }
                }
            }
            // surprise_message <message> or empty for disable
            else if (message.startsWith(SecretMessages.SURPRISE_MESSAGE)) { // Surprise message
                message = message.replace(SecretMessages.SURPRISE_MESSAGE, "").trim();
                if (TextUtils.isEmpty(message)) {
                    Timber.i("Disabling surprise");
                    setSurpriseMessage(null);
                } else {
                    Timber.i("Enabling surprise");
                    setSurpriseMessage(message);
                }
                handled = true;
            }
            // shared_collection_name <name>
            else if (message.startsWith(SecretMessages.SHARED_COLLECTION_NAME)) { // Shared
                String[] parts = message.split(" ");
                String collectionName;
                if (parts.length == 2) {
                    collectionName = parts[1];
                } else {
                    collectionName = null;
                }
                setSharedCollectionName(collectionName);
                Timber.i("Set shared collection name to %s", collectionName);
                handled = true;
            }
            // shared_collection_source <this source> <other source>
            else if (message.startsWith(SecretMessages.SHARED_COLLECTION_SOURCE)) { // Shared
                String[] parts = message.split(" ");
                String source, dest;
                if (parts.length == 3) {
                    source = parts[1];
                    dest = parts[2];
                } else {
                    source = null;
                    dest = null;
                }
                setSharedThisSource(source);
                setSharedOtherSource(dest);
                Timber.i("Set shared source, destination to %1s, %2s", source, dest);
                handled = true;
            }
        }
        if (!handled) {
            Timber.i("Secret message means nothing");
        }
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

    private static void setSurpriseMessage(String message) {
        PrefHelper.set(PrefKeys.SURPRISE_MESSAGE, message);
    }

    public static String getSurpriseMessage() {
        return PrefHelper.get(PrefKeys.SURPRISE_MESSAGE, null);
    }

    private static void setSharedCollectionName(String collectionName) {
        PrefHelper.set(PrefKeys.SHARED_COLLECTION_NAME, collectionName);
    }

    public static String getSharedCollectionName() {
        return PrefHelper.get(PrefKeys.SHARED_COLLECTION_NAME, null);
    }

    private static void setSharedThisSource(String source) {
        PrefHelper.set(PrefKeys.SHARED_THIS_SOURCE, source);
    }

    public static String getSharedThisSource() {
        return PrefHelper.get(PrefKeys.SHARED_THIS_SOURCE, null);
    }

    private static void setSharedOtherSource(String otherSource) {
        PrefHelper.set(PrefKeys.SHARED_OTHER_SOURCE, otherSource);
    }

    public static String getSharedOtherSource() {
        return PrefHelper.get(PrefKeys.SHARED_OTHER_SOURCE, null);
    }
}

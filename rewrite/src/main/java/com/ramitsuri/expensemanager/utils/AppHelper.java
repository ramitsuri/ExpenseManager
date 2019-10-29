package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;

import androidx.annotation.StringRes;

public class AppHelper {

    public static String getAccountName() {
        return PrefHelper.get(getString(R.string.settings_key_account_name), null);
    }

    public static void setAccountName(String accountName) {
        PrefHelper.set(getString(R.string.settings_key_account_name), accountName);
    }

    public static String getAccountType() {
        return PrefHelper.get(getString(R.string.settings_key_account_type), null);
    }

    public static void setAccountType(String accountType) {
        PrefHelper.set(getString(R.string.settings_key_account_type), accountType);
    }

    public static String getSpreadsheetId() {
        return PrefHelper.get(getString(R.string.settings_key_spreadsheet_id), null);
    }

    public static void setSpreadsheetId(String spreadsheetId) {
        PrefHelper.set(getString(R.string.settings_key_spreadsheet_id), spreadsheetId);
    }

    public static String getCurrentSheetId() {
        return PrefHelper.get(getString(R.string.settings_key_sheet_id), null);
    }

    public static void setCurrentSheetId(String currentSheetId) {
        PrefHelper.set(getString(R.string.settings_key_sheet_id), currentSheetId);
    }

    private static String getString(@StringRes int resourceId) {
        return MainApplication.getInstance().getString(resourceId);
    }
}

package com.ramitsuri.expensemanager.utils;

import android.os.Build;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.IntDefs.MigrationStep;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;

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

    public static String getCurrentTheme() {
        return PrefHelper.get(getString(R.string.settings_key_theme), null);
    }

    public static void setCurrentTheme(String theme) {
        switch (theme) {
            case Constants.SystemTheme.LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                PrefHelper.set(getString(R.string.settings_key_theme), Constants.SystemTheme.LIGHT);
                break;

            case Constants.SystemTheme.DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                PrefHelper.set(getString(R.string.settings_key_theme), Constants.SystemTheme.DARK);
                break;

            case Constants.SystemTheme.SYSTEM_DEFAULT:
            case Constants.SystemTheme.BATTERY_SAVER:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Q or newer
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    PrefHelper.set(getString(R.string.settings_key_theme),
                            Constants.SystemTheme.SYSTEM_DEFAULT);
                } else { // Older than Q
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    PrefHelper.set(getString(R.string.settings_key_theme),
                            Constants.SystemTheme.BATTERY_SAVER);
                }
                break;
        }
    }

    public static String getVersionInfo() {
        return BuildConfig.VERSION_NAME;
    }

    public static void setDefaultSheetId(int sheetId) {
        PrefHelper.set(getString(R.string.settings_key_default_sheet_id), sheetId);
    }

    public static int getDefaultSheetId() {
        return PrefHelper
                .get(getString(R.string.settings_key_default_sheet_id), Constants.Basic.UNDEFINED);
    }

    public static String[] getScopes() {
        return Constants.SCOPES_LIMITED;
    }

    private static String getString(@StringRes int resourceId) {
        return MainApplication.getInstance().getString(resourceId);
    }
}

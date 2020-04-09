package com.ramitsuri.expensemanager.utils;

import android.os.Build;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.stringDefs.PrefKeys;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatDelegate;

public class AppHelper {

    public static String getAccountName() {
        return PrefHelper.get(PrefKeys.ACCOUNT_NAME, null);
    }

    public static void setAccountName(String accountName) {
        PrefHelper.set(PrefKeys.ACCOUNT_NAME, accountName);
    }

    public static String getAccountType() {
        return PrefHelper.get(PrefKeys.ACCOUNT_TYPE, null);
    }

    public static void setAccountType(String accountType) {
        PrefHelper.set(PrefKeys.ACCOUNT_TYPE, accountType);
    }

    public static String getSpreadsheetId() {
        return PrefHelper.get(PrefKeys.SPREADSHEET_ID, null);
    }

    public static void setSpreadsheetId(String spreadsheetId) {
        PrefHelper.set(PrefKeys.SPREADSHEET_ID, spreadsheetId);
    }

    public static String getCurrentSheetId() {
        return PrefHelper.get(PrefKeys.SHEET_ID, null);
    }

    public static String getCurrentTheme() {
        return PrefHelper.get(PrefKeys.THEME, null);
    }

    public static void setCurrentTheme(String theme) {
        switch (theme) {
            case Constants.SystemTheme.LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                PrefHelper.set(PrefKeys.THEME, Constants.SystemTheme.LIGHT);
                break;

            case Constants.SystemTheme.DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                PrefHelper.set(PrefKeys.THEME, Constants.SystemTheme.DARK);
                break;

            case Constants.SystemTheme.SYSTEM_DEFAULT:
            case Constants.SystemTheme.BATTERY_SAVER:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Q or newer
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    PrefHelper.set(PrefKeys.THEME, Constants.SystemTheme.SYSTEM_DEFAULT);
                } else { // Older than Q
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    PrefHelper.set(PrefKeys.THEME, Constants.SystemTheme.BATTERY_SAVER);
                }
                break;
        }
    }

    public static String getVersionInfo() {
        return BuildConfig.VERSION_NAME;
    }

    public static boolean isSplittingEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_SPLITTING, false);
    }

    public static void setSplittingEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_SPLITTING, enable);
    }

    public static boolean isExpenseSyncEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_EXPENSE_SYNC, false);
    }

    public static void setExpenseSyncEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_EXPENSE_SYNC, enable);
    }

    public static boolean isEntitiesSyncEnabled() {
        return PrefHelper.get(PrefKeys.ENABLE_ENTITIES_SYNC, false);
    }

    public static void setEntitiesSyncEnabled(boolean enable) {
        PrefHelper.set(PrefKeys.ENABLE_ENTITIES_SYNC, enable);
    }

    public static boolean isEntitiesEdited() {
        return PrefHelper.get(PrefKeys.IS_ENTITIES_EDITED, false);
    }

    public static void setEntitiesEdited(boolean edited) {
        PrefHelper.set(PrefKeys.IS_ENTITIES_EDITED, edited);
    }

    public static TimeZone getTimeZone() {
        String tzId = PrefHelper.get(PrefKeys.TIME_ZONE_ID, null);
        if (!TextUtils.isEmpty(tzId)) {
            return TimeZone.getTimeZone(tzId);
        }

        // Timezone ID unavailable, falling back to device timezone
        setTimeZoneId(TimeZone.getDefault().getID());
        return TimeZone.getDefault();
    }

    public static void setTimeZoneId(String tzId) {
        PrefHelper.set(PrefKeys.TIME_ZONE_ID, tzId);
    }

    public static String[] getScopes() {
        return Constants.SCOPES_LIMITED;
    }

    public static List<String> getMonths() {
        String[] monthArray =
                MainApplication.getInstance().getResources().getStringArray(R.array.months);
        return Arrays.asList(monthArray);
    }
}

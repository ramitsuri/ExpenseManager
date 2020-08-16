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

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatDelegate;

public class AppHelper {
    public static String getSpreadsheetId() {
        return PrefHelper.get(PrefKeys.SPREADSHEET_ID, null);
    }

    public static void setSpreadsheetId(String spreadsheetId) {
        PrefHelper.set(PrefKeys.SPREADSHEET_ID, spreadsheetId);
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

    public static boolean isFirstRunComplete() {
        return PrefHelper.get(PrefKeys.IS_FIRST_RUN_COMPLETE, false);
    }

    public static void setFirstRunComplete(boolean complete) {
        PrefHelper.set(PrefKeys.IS_FIRST_RUN_COMPLETE, complete);
    }

    @Nullable
    public static String getBackupInfoStatus() {
        return PrefHelper.get(PrefKeys.BACKUP_INFO_STATUS, null);
    }

    public static void setBackupInfoStatus(String backupInfoStatus) {
        PrefHelper.set(PrefKeys.BACKUP_INFO_STATUS, backupInfoStatus);
    }

    public static boolean isPruneComplete() {
        return PrefHelper.get(PrefKeys.IS_PRUNE_COMPLETE, false);
    }

    public static void setPruneComplete(boolean complete) {
        PrefHelper.set(PrefKeys.IS_PRUNE_COMPLETE, complete);
    }

    public static boolean isBackupIssueFixed() {
        return PrefHelper.get(PrefKeys.BACKUP_ISSUE_FIXED, false);
    }

    public static void setBackupIssueFixed(boolean fixed) {
        PrefHelper.set(PrefKeys.BACKUP_ISSUE_FIXED, fixed);
    }
}

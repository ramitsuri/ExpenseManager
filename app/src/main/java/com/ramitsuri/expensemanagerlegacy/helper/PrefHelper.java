package com.ramitsuri.expensemanagerlegacy.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ramitsuri.expensemanagerlegacy.MainApplication;

public class PrefHelper {

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance());
    }

    public static void set(String prefName, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(prefName, value);
        editor.apply();
    }

    public static void set(String prefName, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (value != null) {
            editor.putString(prefName, value);
        } else {
            editor.remove(prefName);
        }
        editor.apply();
    }

    public static void set(String prefName, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(prefName, value);
        editor.apply();
    }

    public static void set(String prefName, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(prefName, value);
        editor.apply();
    }

    public static void remove(String prefName) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(prefName);
        editor.apply();
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

    public static boolean get(String prefName, boolean defaultValue) {
        return getSharedPreferences().getBoolean(prefName, defaultValue);
    }

    public static String get(String prefName, String defaultValue) {
        return getSharedPreferences().getString(prefName, defaultValue);
    }

    public static int get(String prefName, int defaultValue) {
        return getSharedPreferences().getInt(prefName, defaultValue);
    }

    public static long get(String prefName, long defaultValue) {
        return getSharedPreferences().getLong(prefName, defaultValue);
    }
}

package com.ramitsuri.expensemanager.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.DateHelper;

import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

    public interface PreferenceClickCallbacks{
        void onBackupNowClicked();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private Preference mBackupNow;
        private EditTextPreference mSheetsId;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mBackupNow = findPreference(getString(R.string.preference_key_backup_now));
            mSheetsId = (EditTextPreference)findPreference(
                    getString(R.string.preference_key_sheets_id));
            mBackupNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    return false;
                }
            });
            mSheetsId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return false;
                }
            });
            updatePreferenceSummaries();
        }

        private void updatePreferenceSummaries() {
            String sheetsId = AppHelper.getSheetsId();
            if (sheetsId != null) {
                mSheetsId.setSummary(sheetsId);
            }
            long lastBackupTime = AppHelper.getLastBackupTimeInMillis();
            if (lastBackupTime != 0) {
                mBackupNow.setSummary(DateHelper.getDateTimeFromTimeInMillis(lastBackupTime));
            }
        }
    }
}

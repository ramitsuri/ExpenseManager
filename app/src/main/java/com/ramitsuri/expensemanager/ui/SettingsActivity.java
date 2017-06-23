package com.ramitsuri.expensemanager.ui;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.async.SheetsBackupTask;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.DateHelper;

public class SettingsActivity extends AppCompatPreferenceActivity {

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
                    new SheetsBackupTask(getContext()){
                        @Override
                        protected void onPostExecute(LoaderResponse loaderResponse) {
                            super.onPostExecute(loaderResponse);
                            if(loaderResponse.getResponseCode() == LoaderResponse.SUCCESS){
                                AppHelper.setLastBackupTime(System.currentTimeMillis());
                                updatePreferenceSummaries();
                            }
                        }
                    }.execute();
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

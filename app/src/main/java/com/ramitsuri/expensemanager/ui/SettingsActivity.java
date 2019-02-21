package com.ramitsuri.expensemanager.ui;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.async.SheetsBackupTask;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.DateHelper;

import java.util.List;

import androidx.work.WorkManager;
import androidx.work.WorkStatus;

import static com.ramitsuri.expensemanager.constants.Others.REQUEST_AUTHORIZATION;

public class SettingsActivity extends AppCompatPreferenceActivity {
    SettingsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mFragment).commit();

        LiveData<List<WorkStatus>> statuses = WorkManager.getInstance().getStatusesByTag("Backup");
        if (statuses.getValue() != null) {
            for (WorkStatus status : statuses.getValue()) {
                Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
            }
        }
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
                    ((SettingsActivity)getActivity()).backup();
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

    private void backup() {
        new SheetsBackupTask(this) {
            @Override
            protected void onPostExecute(LoaderResponse loaderResponse) {
                super.onPostExecute(loaderResponse);
                if (loaderResponse.getResponseCode() == LoaderResponse.SUCCESS) {
                    AppHelper.setLastBackupTime(System.currentTimeMillis());
                    mFragment.updatePreferenceSummaries();
                } else if (loaderResponse.getResponseCode() == LoaderResponse.FAILURE) {
                    startActivityForResult(loaderResponse.getIntent(), REQUEST_AUTHORIZATION);
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK && data != null) {
                    backup();
                }
        }
    }
}

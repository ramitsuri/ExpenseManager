package com.ramitsuri.expensemanagerlegacy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.async.SheetsBackupTask;
import com.ramitsuri.expensemanagerlegacy.entities.LoaderResponse;
import com.ramitsuri.expensemanagerlegacy.helper.AppHelper;
import com.ramitsuri.expensemanagerlegacy.helper.DateHelper;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.LiveData;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.ramitsuri.expensemanagerlegacy.constants.Others.REQUEST_AUTHORIZATION;

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

        LiveData<List<WorkInfo>> infos =
                WorkManager.getInstance().getWorkInfosByTagLiveData("Backup");
        if (infos.getValue() != null) {
            for (WorkInfo status : infos.getValue()) {
                Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        private Preference mBackupNow;
        private EditTextPreference mSpreadsheetId;
        private EditTextPreference mSheetsId;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mBackupNow = findPreference(getString(R.string.preference_key_backup_now));

            mSpreadsheetId = (EditTextPreference)findPreference(
                    getString(R.string.preference_key_spreadsheet_id));
            mSheetsId = (EditTextPreference)findPreference(
                    getString(R.string.preference_key_sheets_id));
            mBackupNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    ((SettingsActivity)getActivity()).backup();
                    return false;
                }
            });
            mSpreadsheetId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
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
            String spreadsheetId = AppHelper.getSpreadsheetId();
            if (spreadsheetId != null) {
                mSpreadsheetId.setSummary(spreadsheetId);
            }
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

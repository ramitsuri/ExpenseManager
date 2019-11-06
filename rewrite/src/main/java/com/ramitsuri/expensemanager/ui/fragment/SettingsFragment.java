package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.Menu;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Auto backup
        final SwitchPreferenceCompat autoBackupPref =
                findPreference(getString(R.string.settings_key_auto_backup));
        if (autoBackupPref != null) {
            setAutoBackupPrefText(autoBackupPref, autoBackupPref.isChecked());
            autoBackupPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean enabled = (boolean)newValue;
                            setAutoBackupPrefText(autoBackupPref, enabled);
                            enableOrDisableAutoBackup(enabled);
                            return true;
                        }
                    });
        }

        // System theme
        ListPreference themePref = findPreference(getString(R.string.settings_key_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setAppTheme((String)newValue);
                    return true;
                }
            });
        }
    }

    private void enableOrDisableAutoBackup(boolean enabled) {
        if (enabled) {
            WorkHelper.enqueuePeriodicBackup();
        } else {
            WorkHelper.cancelScheduledBackup();
        }
    }

    private void setAutoBackupPrefText(SwitchPreferenceCompat pref, boolean isChecked) {
        if (isChecked) {
            pref.setSummary(pref.getSwitchTextOn());
        } else {
            pref.setSummary(pref.getSwitchTextOff());
        }
    }

    private void setAppTheme(String theme) {
        AppHelper.setCurrentTheme(theme);
    }
}

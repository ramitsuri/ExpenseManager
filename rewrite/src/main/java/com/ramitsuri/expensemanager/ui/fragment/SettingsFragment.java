package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.ToastHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragmentCompat {

    private int mPressCount;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Activity activity = getActivity();
                if (activity != null) {
                    ((AppCompatActivity)activity).onSupportNavigateUp();
                } else {
                    Timber.w("handleCloseFragmentClicked() -> Activity is null");
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mPressCount = 0;

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

        // Version info
        Preference versionInfoPref = findPreference(getString(R.string.settings_key_version_info));
        if (versionInfoPref != null) {
            versionInfoPref.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            mPressCount = mPressCount + 1;
                            if (mPressCount >= 7) {
                                AppHelper.enableDebugOptions();
                                if (getActivity() != null) {
                                    ToastHelper.showToast(getActivity(),
                                            R.string.settings_hidden_options_enabled);
                                }
                            }
                            return true;
                        }
                    });
            versionInfoPref.setSummary(AppHelper.getVersionInfo());
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
        Timber.i("Setting theme to %s", theme);
        AppHelper.setCurrentTheme(theme);
    }
}

package com.ramitsuri.expensemanager.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.utils.ToastHelper;
import com.ramitsuri.expensemanager.viewModel.MiscellaneousViewModel;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class MiscellaneousFragment extends BaseFragment {

    private MiscellaneousViewModel mViewModel;

    public MiscellaneousFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_miscellaneous, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitToUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        showActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideActionBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MiscellaneousViewModel.class);

        // Header - Backup and Sync
        setupHeader(view,
                R.id.header_backup_sync,
                R.string.header_title_backup_sync);

        // Backup
        setupMenuItem(view,
                R.id.item_backup,
                R.string.common_sync_now,
                R.drawable.ic_backup);

        // Auto backup
        setupAutoBackupItem(view,
                R.id.item_auto_backup,
                R.string.settings_title_auto_backup,
                R.drawable.ic_auto_backup);

        // Sync
        setupMenuItem(view,
                R.id.item_sync,
                R.string.common_sync,
                R.drawable.ic_sync);

        // Logs Metadata
        setupMenuItem(view,
                R.id.item_sheets,
                R.string.action_meta_data,
                R.drawable.ic_logs,
                mViewModel.enableHidden());

        // Delete all
        setupMenuItem(view, R.id.item_delete_all,
                R.string.action_delete,
                R.drawable.ic_delete,
                mViewModel.enableDeleteAll());

        // Header - Spreadsheet
        setupHeader(view,
                R.id.header_spreadsheet,
                R.string.header_title_spreadsheet);

        // Spreadsheet Id
        setupSpreadsheetItem(view,
                R.id.item_spreadsheet_id,
                R.string.settings_title_spreadsheet_id,
                R.drawable.ic_spreadsheet_id);

        // Header - General
        setupHeader(view,
                R.id.header_general,
                R.string.header_title_general);

        // Theme
        setupThemeItem(view,
                R.id.item_theme,
                R.string.settings_title_theme,
                R.drawable.ic_theme);

        // Version
        setupVersionItem(view,
                R.id.item_version,
                R.string.settings_title_version_info,
                R.drawable.ic_version);
    }

    @Nullable
    private ViewGroup setupMenuItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes) {
        return setupMenuItem(view, idRes, titleRes, drawableRes, true);
    }

    @Nullable
    private ViewGroup setupMenuItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes,
            boolean show) {
        if (!show) {
            return null;
        }
        ViewGroup container = view.findViewById(idRes);
        if (container != null) {
            // Title
            TextView title = container.findViewById(R.id.title);
            if (title != null) {
                title.setText(titleRes);
            }

            // Icon
            ImageView icon = container.findViewById(R.id.icon);
            if (icon != null) {
                icon.setImageResource(drawableRes);
            }

            // Click listener
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleMenuItemClicked(idRes);
                }
            });
            container.setVisibility(View.VISIBLE);
        }
        return container;
    }

    private void setupHeader(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes) {
        TextView title = view.findViewById(idRes);
        if (title != null) {
            title.setText(titleRes);
        }
    }

    private void setupAutoBackupItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes);
        if (container != null) {
            boolean autoBackupEnabled = mViewModel.isAutoBackupEnabled();
            // Summary
            final TextView summary = container.findViewById(R.id.summary);
            summary.setVisibility(View.VISIBLE);
            onAutoBackupStatusChanged(summary, autoBackupEnabled, false);

            // Switch
            final SwitchCompat toggle = view.findViewById(R.id.toggle);
            toggle.setChecked(autoBackupEnabled);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAutoBackupStatusChanged(summary, isChecked, true);
                }
            });
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle.setChecked(!toggle.isChecked());
                }
            });
        }
    }

    private void onAutoBackupStatusChanged(TextView summary, boolean enabled, boolean updateAll) {
        if (enabled) {
            summary.setText(R.string.settings_auto_backup_on);
        } else {
            summary.setText(R.string.settings_auto_backup_off);
        }
        if (updateAll) {
            mViewModel.setAutoBackupStatus(enabled);
        }
    }

    private void setupVersionItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes);
        if (container != null) {
            // Summary
            TextView summary = container.findViewById(R.id.summary);
            if (summary != null) {
                summary.setText(mViewModel.getVersionInfo());
                summary.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupSpreadsheetItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes);
        if (container != null) {
            // Summary
            final TextView summary = container.findViewById(R.id.summary);
            if (summary != null) {
                mViewModel.getSpreadsheetIdLive().observe(getViewLifecycleOwner(),
                        new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                summary.setText(s);
                            }
                        });
                summary.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupThemeItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @DrawableRes int drawableRes) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes);
        if (container != null) {
            // Summary
            final TextView summary = container.findViewById(R.id.summary);
            if (summary != null) {
                mViewModel.getSelectedThemeLive().observe(getViewLifecycleOwner(),
                        new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                if (s != null) {
                                    if (s.equals(Constants.SystemTheme.LIGHT)) {
                                        summary.setText(R.string.theme_light);
                                    } else if (s.equals(Constants.SystemTheme.DARK)) {
                                        summary.setText(R.string.theme_dark);
                                    } else if (s.equals(Constants.SystemTheme.SYSTEM_DEFAULT)) {
                                        summary.setText(R.string.theme_system_default);
                                    } else {
                                        summary.setText(R.string.theme_battery_saver);
                                    }
                                }
                            }
                        });
                summary.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleMenuItemClicked(@IdRes int idRes) {
        switch (idRes) {
            case R.id.item_backup:
                mViewModel.initiateBackup();
                break;

            case R.id.item_sync:
                mViewModel.syncDataFromSheet();
                break;

            case R.id.item_sheets:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_sheets, null);
                break;

            case R.id.item_delete_all:
                mViewModel.deleteExpenses();
                break;

            case R.id.item_spreadsheet_id:
                Context context = this.getContext();
                if (context == null) {
                    return;
                }
                final EditText input = new EditText(context);
                input.setText(mViewModel.getSpreadsheetId());
                input.setSelection(input.getText().length());
                DialogInterface.OnClickListener positiveListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.setSpreadsheetId(input.getText().toString());
                            }
                        };

                DialogHelper.showAlertWithInput(context,
                        input,
                        R.string.settings_title_spreadsheet_id,
                        R.string.common_ok, positiveListener,
                        R.string.common_cancel, null);
                break;

            case R.id.item_version:
                if (mViewModel.versionInfoPressSuccess()) {
                    if (getActivity() != null) {
                        ToastHelper.showToast(getActivity(),
                                R.string.settings_hidden_options_enabled);
                    }
                }
                break;

            case R.id.item_theme:
                context = this.getContext();
                if (context == null) {
                    return;
                }

                int themeTitles = mViewModel.getThemeTitles();
                int selectedTheme = mViewModel.getSelectedTheme();
                DialogInterface.OnClickListener itemListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.setTheme(which);
                                dialog.dismiss();
                            }
                        };

                DialogHelper.showAlertList(context,
                        themeTitles, selectedTheme, itemListener,
                        R.string.settings_title_theme,
                        R.string.common_cancel, null);
                break;
        }
    }
}

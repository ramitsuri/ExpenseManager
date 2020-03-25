package com.ramitsuri.expensemanager.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.MiscellaneousViewModel;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MiscellaneousViewModel.class);

        setupViews(view);
    }

    private void setupViews(@Nonnull View view) {
        // Close
        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });

        // Header - Backup and Sync
        setupHeader(view,
                R.id.header_backup_sync,
                R.string.header_title_backup_sync,
                mViewModel.enableHidden());

        // Backup
        setupMenuItem(view,
                R.id.item_backup,
                R.string.common_sync_now,
                R.drawable.ic_backup,
                mViewModel.enableHidden());

        // Sync
        setupMenuItem(view,
                R.id.item_sync,
                R.string.common_sync,
                R.drawable.ic_sync,
                mViewModel.enableHidden());

        // Sync Expenses
        setupMenuItem(view,
                R.id.item_sync_expenses,
                R.string.common_sync_expenses,
                R.drawable.ic_sync,
                mViewModel.enableHidden());

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
                R.string.header_title_spreadsheet,
                mViewModel.enableHidden());

        // Spreadsheet Id
        setupSpreadsheetItem(view,
                R.id.item_spreadsheet_id,
                R.string.settings_title_spreadsheet_id,
                R.drawable.ic_spreadsheet_id,
                mViewModel.enableHidden());

        // Header - General
        setupHeader(view,
                R.id.header_general,
                R.string.header_title_general,
                true);

        // Edit entities
        setupMenuItem(view,
                R.id.item_edit_entities,
                R.string.miscellaneous_sync_entities,
                R.drawable.ic_edit_entities,
                BuildConfig.DEBUG);

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
            @StringRes int titleRes,
            boolean show) {
        if (!show) {
            return;
        }
        TextView title = view.findViewById(idRes);
        if (title != null) {
            title.setText(titleRes);
            title.setVisibility(View.VISIBLE);
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
            @DrawableRes int drawableRes,
            boolean show) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes, show);
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

            case R.id.item_sync_expenses:
                mViewModel.syncExpensesFromSheet();
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
                if (mViewModel.versionInfoPressSuccess() && getView() != null) {
                    setupViews(getView());
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

            case R.id.item_edit_entities:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_first_setup, null);
                break;
        }
    }
}

package com.ramitsuri.expensemanager.ui.fragment;

import android.accounts.Account;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.stringDefs.BackupInfoStatus;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.MiscellaneousViewModel;
import com.ramitsuri.sheetscore.googleSignIn.AccountManager;

import javax.annotation.Nonnull;

import timber.log.Timber;

import static com.ramitsuri.expensemanager.constants.Constants.RequestCode.GOOGLE_SIGN_IN;

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
        Button btnClose = view.findViewById(R.id.btn_close);
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
                true);

        // Backup Info
        setupBackupInfoItem(view,
                R.id.item_backup_info,
                R.string.miscellaneous_backup_info_title,
                R.drawable.ic_backup,
                true);

        // Backup
        setupMenuItem(view,
                R.id.item_backup,
                R.string.common_sync_now,
                R.drawable.ic_backup,
                mViewModel.enableBackupNow());

        // Sync
        setupMenuItem(view,
                R.id.item_sync,
                R.string.common_sync,
                R.drawable.ic_sync,
                mViewModel.enableEntitiesSync());

        // Sync Expenses
        setupMenuItem(view,
                R.id.item_sync_expenses,
                R.string.common_sync_expenses,
                R.drawable.ic_sync,
                mViewModel.enableExpenseSync());

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

        // Header - Entities
        setupHeader(view,
                R.id.header_entities,
                R.string.header_title_entities,
                true);

        // Edit Categories
        setupMenuItem(view,
                R.id.item_edit_categories,
                R.string.common_categories,
                R.drawable.ic_category,
                true);

        // Edit Payment Methods
        setupMenuItem(view,
                R.id.item_edit_payment_methods,
                R.string.common_payment_methods,
                R.drawable.ic_payment_method,
                true);

        // Edit Budgets
        setupMenuItem(view,
                R.id.item_edit_budgets,
                R.string.common_budgets,
                R.drawable.ic_budget,
                true);

        // Header - General
        setupHeader(view,
                R.id.header_general,
                R.string.header_title_general,
                true);

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
            final String spreadsheetId = mViewModel.getSpreadsheetId();
            if (TextUtils.isEmpty(spreadsheetId)) {
                return;
            }
            // Summary
            final TextView summary = container.findViewById(R.id.summary);
            if (summary != null) {
                summary.setText(spreadsheetId);
                summary.setVisibility(View.VISIBLE);
            }
            container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return false;
                    }
                    ClipboardManager clipboard =
                            (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard == null) {
                        return false;
                    }
                    ClipData clip = ClipData.newPlainText(spreadsheetId, spreadsheetId);
                    clipboard.setPrimaryClip(clip);
                    return false;
                }
            });
        }
    }

    private void setupBackupInfoItem(@Nonnull View view,
                                     @IdRes final int idRes,
                                     @StringRes int titleRes,
                                     @DrawableRes int drawableRes,
                                     boolean show) {
        ViewGroup container = setupMenuItem(view, idRes, titleRes, drawableRes, show);
        if (container != null) {
            // Progress
            final View progress = container.findViewById(R.id.progress);
            // Summary
            final TextView summary = container.findViewById(R.id.summary);
            if (summary != null) {
                summary.setVisibility(View.VISIBLE);
                mViewModel.getBackupInfoStatus().observe(getViewLifecycleOwner(),
                        new Observer<String>() {
                            @Override
                            public void onChanged(String status) {
                                if (TextUtils.isEmpty(status)) {
                                    return;
                                }
                                switch (status) {
                                    case BackupInfoStatus.NO:
                                        summary.setText(
                                                R.string.miscellaneous_backup_info_not_enabled);
                                        if (progress != null) {
                                            progress.setVisibility(View.GONE);
                                        }
                                        break;

                                    case BackupInfoStatus.MAYBE:
                                        summary.setText(R.string.miscellaneous_backup_info_enabled);
                                        if (progress != null) {
                                            progress.setVisibility(View.GONE);
                                        }
                                        break;

                                    case BackupInfoStatus.CREATING:
                                        summary.setText(
                                                R.string.miscellaneous_backup_info_creating);
                                        if (progress != null) {
                                            progress.setVisibility(View.VISIBLE);
                                        }
                                        break;

                                    case BackupInfoStatus.ERROR:
                                        summary.setText(R.string.miscellaneous_backup_info_error);
                                        if (progress != null) {
                                            progress.setVisibility(View.GONE);
                                        }
                                        break;

                                    case BackupInfoStatus.OK:
                                        summary.setText(R.string.miscellaneous_backup_info_good);
                                        if (progress != null) {
                                            progress.setVisibility(View.GONE);
                                        }
                                        break;
                                }
                            }
                        });
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

            case R.id.item_version:
                if (mViewModel.versionInfoPressSuccess() && getView() != null) {
                    setupViews(getView());
                }
                break;

            case R.id.item_theme:
                Context context = this.getContext();
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

            case R.id.item_edit_categories:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_categories_setup, null);
                break;

            case R.id.item_edit_budgets:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_budgets_setup, null);
                break;

            case R.id.item_edit_payment_methods:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_payment_methods_setup, null);
                break;

            case R.id.item_backup_info:
                handleBackupInfoClicked();
                break;
        }
    }

    private void handleBackupInfoClicked() {
        Account account = mViewModel.getSignInAccount();
        if (account == null) {
            Timber.i("Account null, attempting sign in");
            signIn();
            return;
        } else {
            Timber.i("Signed in as %s", account.name);
        }
        // Create or possibly fetch existing spreadsheet
        LiveData<Boolean> resultReceived = mViewModel.onBackupInfoClicked();
        if (resultReceived != null) {
            resultReceived.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean != null) {
                        Timber.i("Something happened as a result of onBackupInfoClicked");
                    }
                }
            });
        }
    }

    private void signIn() {
        Intent intent = mViewModel.getSignInIntent();
        if (intent != null) {
            Timber.i("Sign in intent received, requesting auth");
            startActivityForResult(intent, GOOGLE_SIGN_IN);
        } else {
            Timber.i("Sign in intent null, dead end");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GOOGLE_SIGN_IN) {
            Account account = AccountManager.getSignInAccountFromIntent(data);
            if (account != null) {
                handleBackupInfoClicked();
            } else {
                Timber.i("Activity returned null account");
                if (data != null && data.getExtras() != null && !data.getExtras().isEmpty()) {
                    Timber.w("Information returned for error: %s", data.getExtras());
                    if (getView() != null) {
                        Snackbar.make(getView(), R.string.miscellaneous_sign_in_fail_message,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

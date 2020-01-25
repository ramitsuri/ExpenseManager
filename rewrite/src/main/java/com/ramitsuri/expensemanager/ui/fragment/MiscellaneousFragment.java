package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.viewModel.MiscellaneousViewModel;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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
        hideActionBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MiscellaneousViewModel.class);

        // Backup
        setupMenuItem(view,
                R.id.item_backup,
                R.string.common_sync_now,
                Constants.UNDEFINED,
                R.drawable.ic_backup);

        // Sync
        setupMenuItem(view,
                R.id.item_sync,
                R.string.common_sync,
                Constants.UNDEFINED,
                R.drawable.ic_sync);

        // Settings
        setupMenuItem(view,
                R.id.item_settings,
                R.string.action_settings,
                Constants.UNDEFINED,
                R.drawable.ic_settings);

        // Logs Metadata
        setupMenuItem(view,
                R.id.item_sheets,
                R.string.action_meta_data,
                Constants.UNDEFINED,
                R.drawable.ic_logs,
                mViewModel.enableHidden());

        // Delete all
        setupMenuItem(view, R.id.item_delete_all,
                R.string.action_delete,
                Constants.UNDEFINED,
                R.drawable.ic_delete,
                mViewModel.enableDeleteAll());
    }

    private void setupMenuItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @StringRes int summaryRes,
            @DrawableRes int drawableRes) {
        setupMenuItem(view, idRes, titleRes, summaryRes, drawableRes, true);
    }

    private void setupMenuItem(@Nonnull View view,
            @IdRes final int idRes,
            @StringRes int titleRes,
            @StringRes int summaryRes,
            @DrawableRes int drawableRes,
            boolean show) {
        if (!show) {
            return;
        }
        ViewGroup container = view.findViewById(idRes);
        if (container != null) {
            // Title
            TextView title = container.findViewById(R.id.title);
            if (title != null) {
                title.setText(titleRes);
            }

            // Summary
            if (summaryRes != Constants.UNDEFINED) {
                TextView summary = container.findViewById(R.id.summary);
                if (summary != null) {
                    summary.setText(summaryRes);
                }
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

            case R.id.item_settings:
                NavHostFragment.findNavController(MiscellaneousFragment.this)
                        .navigate(R.id.nav_action_settings, null);
                break;

            case R.id.item_delete_all:
                mViewModel.deleteExpenses();
                break;
        }
    }
}

package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.ui.adapter.BarAdapter;
import com.ramitsuri.expensemanager.ui.adapter.BarWrapper;
import com.ramitsuri.expensemanager.viewModel.AnalysisViewModel;
import com.ramitsuri.expensemanager.viewModel.ViewModelFactory;

import java.util.List;

import timber.log.Timber;

public class AnalysisFragment extends BaseBottomSheetFragment {
    static final String TAG = AnalysisFragment.class.getName();

    static AnalysisFragment newInstance() {
        return new AnalysisFragment();
    }

    @Nullable
    private AnalysisViewModel mViewModel;

    // Data
    private BarAdapter mBarAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        setSystemUiVisibility(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            List<Expense> expenses =
                    getArguments().getParcelableArrayList(Constants.BundleKeys.ANALYSIS_EXPENSES);
            Timber.i("Received expenses");
            mViewModel = new ViewModelProvider(this, new ViewModelFactory(expenses))
                    .get(AnalysisViewModel.class);
        }

        // Views
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    onBudgetTabSelected();
                } else if (tab.getPosition() == 1) {
                    onPaymentsTabSelected();
                } else if (tab.getPosition() == 2) {
                    onCategoriesTabSelected();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupList(view);

        // Default
        if (mViewModel != null && mViewModel.isCalculated() != null) {
            mViewModel.isCalculated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    Timber.i("Calculation done");
                    onBudgetTabSelected();
                }
            });
        }
    }

    private void setupList(View view) {
        Timber.i("Setting up list view");
        RecyclerView list = view.findViewById(R.id.list_bars);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBarAdapter = new BarAdapter();
        list.setAdapter(mBarAdapter);
        if (mViewModel != null && mViewModel.getWrappers() != null) {
            mViewModel.getWrappers().observe(getViewLifecycleOwner(),
                    new Observer<List<BarWrapper>>() {
                        @Override
                        public void onChanged(List<BarWrapper> barWrappers) {
                            Timber.i("Refreshing wrappers");
                            mBarAdapter.setItems(barWrappers);
                        }
                    });
        }
    }

    private void onBudgetTabSelected() {
        Timber.i("Budget tab selected");
        if (mViewModel == null) {
            Timber.i("ViewModel is null");
            return;
        }
        mViewModel.onBudgetTabSelected(getString(R.string.analysis_overall),
                getString(R.string.analysis_used_format),
                getString(R.string.analysis_remaining_format),
                getString(R.string.analysis_overused_format));
    }

    private void onPaymentsTabSelected() {
        Timber.i("Payments tab selected");
        if (mViewModel == null) {
            Timber.i("ViewModel is null");
            return;
        }
        mViewModel.onPaymentsTabSelected(getString(R.string.analysis_overall));
    }

    private void onCategoriesTabSelected() {
        Timber.i("Categories tab selected");
        if (mViewModel == null) {
            Timber.i("ViewModel is null");
            return;
        }
        mViewModel.onCategoriesTabSelected(getString(R.string.analysis_overall));
    }
}

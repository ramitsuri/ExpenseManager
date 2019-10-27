package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.utils.PrefHelper;
import com.ramitsuri.expensemanager.viewModel.ExpensesViewModel;
import com.ramitsuri.expensemanager.work.BackupWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import timber.log.Timber;

public class ExpensesFragment extends BaseFragment {

    private ExpensesViewModel mExpensesViewModel;

    // Views
    private FloatingActionButton mBtnAdd;
    private Button mBtnSyncNow;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mExpensesViewModel = ViewModelProviders.of(this).get(ExpensesViewModel.class);

        mBtnAdd = view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ExpensesFragment.this)
                        .navigate(R.id.nav_action_add_expense, null);
            }
        });

        mBtnSyncNow = view.findViewById(R.id.btn_sync_now);
        mBtnSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountName =
                        PrefHelper.get(getString(R.string.settings_key_account_name), null);
                String accountType =
                        PrefHelper.get(getString(R.string.settings_key_account_type), null);
                if (accountName != null && accountType != null) {
                    initiateBackup(accountName, accountType, false);
                } else {
                    Timber.w("AccountType or Name null. Name " + accountName + ", Type " +
                            accountType);
                }
            }
        });

        setupListExpenses(view);
    }

    private void setupListExpenses(View view) {
        final RecyclerView listExpenses = view.findViewById(R.id.list_expenses);
        listExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ExpenseAdapter adapter = new ExpenseAdapter();
        listExpenses.setAdapter(adapter);
        mExpensesViewModel.getExpenses().observe(this, new Observer<List<ExpenseWrapper>>() {
            @Override
            public void onChanged(List<ExpenseWrapper> expenses) {
                Timber.i("Refreshing expenses");
                adapter.setExpenses(expenses);
            }
        });
        listExpenses.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mBtnAdd.hide();
                } else if (dy < 0) {
                    mBtnAdd.show();
                }
            }
        });

        adapter.setCallback(new ExpenseAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(@NonNull ExpenseWrapper wrapper) {
                showExpenseDetails(wrapper);
            }
        });
    }

    private void showExpenseDetails(ExpenseWrapper wrapper) {
        Timber.i("Showing information for %s", wrapper.toString());
        ExpenseDetailsFragment detailsFragment = ExpenseDetailsFragment.newInstance();
        detailsFragment.setCallback(new ExpenseDetailsFragment.DetailFragmentCallback() {
            @Override
            public void onEditRequested(@NonNull Expense expense) {
                handleExpenseEditRequested();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.SELECTED_EXPENSE, wrapper.getExpense());
        detailsFragment.setArguments(bundle);
        if (getActivity() != null) {
            detailsFragment
                    .show(getActivity().getSupportFragmentManager(), ExpenseDetailsFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void handleExpenseEditRequested() {

    }

    private void initiateBackup(String accountName, String accountType, boolean periodic) {
        Timber.i("Initiating backup");
        String workTag = Constants.Tag.SCHEDULED_BACKUP;

        // Input data
        String spreadsheetId =
                PrefHelper.get(getString(R.string.settings_key_spreadsheet_id), null);
        String sheetId = PrefHelper.get(getString(R.string.settings_key_sheet_id), null);
        Data.Builder builder = new Data.Builder();
        builder.putString(Constants.Work.APP_NAME, getString(R.string.app_name));
        builder.putString(Constants.Work.ACCOUNT_NAME, accountName);
        builder.putString(Constants.Work.ACCOUNT_TYPE, accountType);
        builder.putString(Constants.Work.SPREADSHEET_ID, spreadsheetId);
        builder.putString(Constants.Work.SHEET_ID, sheetId);
        Constraints myConstraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();

        if (periodic) {
            // Request
            PeriodicWorkRequest.Builder periodicWorkRequestBuilder =
                    new PeriodicWorkRequest.Builder(BackupWorker.class, 12, TimeUnit.HOURS)
                            .setConstraints(myConstraints)
                            .setInputData(builder.build())
                            .addTag(workTag);
            PeriodicWorkRequest request = periodicWorkRequestBuilder.build();

            // Enqueue
            WorkManager.getInstance(MainApplication.getInstance())
                    .enqueueUniquePeriodicWork(workTag,
                            ExistingPeriodicWorkPolicy.REPLACE, request);
        } else {
            OneTimeWorkRequest backupRequest = new OneTimeWorkRequest.Builder(BackupWorker.class)
                    .addTag(Constants.Tag.ONE_TIME_BACKUP)
                    .setInputData(builder.build())
                    .build();
            WorkManager.getInstance(MainApplication.getInstance()).enqueue(backupRequest);
        }

        // Status
        logWorkStatus(workTag);
    }
}

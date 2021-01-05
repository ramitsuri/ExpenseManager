package com.ramitsuri.expensemanager.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.ListItemType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseWrapper;
import com.ramitsuri.expensemanager.ui.decoration.StickyHeaderItemDecoration;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;
import com.ramitsuri.expensemanager.viewModel.AllExpensesViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class AllExpensesFragment extends BaseFragment implements View.OnClickListener {

    private AllExpensesViewModel mViewModel;

    // Views
    private ExtendedFloatingActionButton mBtnAdd;
    private RecyclerView mListExpenses;
    private ViewGroup mCardInfo;
    private ViewGroup mGroupButtons;
    private TextView mTextInfoEmpty, mTextInfo1, mTextInfo2, mTextInfo3;
    private Button mBtnFilterSecond, mBtnSetupSecond, mBtnAddSecond, mBtnSharedSecond, mBtnFilter,
            mBtnSetup, mBtnAnalysis, mBtnClearFilter, mBtnShared;

    public AllExpensesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AllExpensesViewModel.class);

        mBtnFilterSecond = view.findViewById(R.id.btn_filter_second);
        mBtnFilterSecond.setOnClickListener(this);

        mBtnSetupSecond = view.findViewById(R.id.btn_setup_second);
        mBtnSetupSecond.setOnClickListener(this);

        mBtnFilter = view.findViewById(R.id.btn_filter);
        mBtnFilter.setOnClickListener(this);

        mBtnSetup = view.findViewById(R.id.btn_setup);
        mBtnSetup.setOnClickListener(this);

        mBtnAdd = view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(this);

        mBtnAddSecond = view.findViewById(R.id.btn_add_expense_second);
        mBtnAddSecond.setOnClickListener(this);

        mBtnAnalysis = view.findViewById(R.id.btn_analysis);
        mBtnAnalysis.setOnClickListener(this);

        mBtnClearFilter = view.findViewById(R.id.btn_clear_filter);
        mBtnClearFilter.setOnClickListener(this);

        mBtnShared = view.findViewById(R.id.btn_getShared);
        if (mViewModel.isEnableSharedExpenses()) {
            mBtnShared.setVisibility(View.VISIBLE);
        }
        mBtnShared.setOnClickListener(this);

        mBtnSharedSecond = view.findViewById(R.id.btn_get_shared_second);
        mBtnSharedSecond.setOnClickListener(this);

        // Shown when no expenses
        mTextInfoEmpty = view.findViewById(R.id.txt_expense_empty);

        // Shown when there are expenses
        mCardInfo = view.findViewById(R.id.card_info);
        mGroupButtons = view.findViewById(R.id.group_buttons);
        mTextInfo1 = view.findViewById(R.id.txt_expense_info_1);
        mTextInfo2 = view.findViewById(R.id.txt_expense_info_2);
        mTextInfo3 = view.findViewById(R.id.txt_expense_info_3);

        mViewModel.getFilterInfo().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filterInfo) {
                if (TextUtils.isEmpty(filterInfo)) {
                    filterInfo = getString(R.string.common_filter);
                    mBtnClearFilter.setVisibility(View.VISIBLE);
                } else {
                    mBtnClearFilter.setVisibility(View.GONE);
                }
                mTextInfoEmpty.setText(String.format(getString(R.string.all_expenses_empty_message),
                        filterInfo));
                mTextInfo1.setText(filterInfo);
            }
        });

        setupListExpenses(view);

        onFilterApplied(null);

        logWorkStatus(WorkHelper.getPeriodicExpensesBackupTag());
        logWorkStatus(WorkHelper.getOneTimeWorkTag());
    }

    private void setupListExpenses(View view) {
        mListExpenses = view.findViewById(R.id.list_expenses);
        final int numberOfColumns = getResources().getInteger(R.integer.expenses_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);

        final ExpenseAdapter adapter = new ExpenseAdapter();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(
                    int position) { // Set headers to span the whole width of recycler view
                @ListItemType
                int itemType = adapter.getItemViewType(position);
                switch (itemType) {
                    case ListItemType.HEADER:
                        return numberOfColumns;

                    case ListItemType.ITEM:

                    default:
                        return 1; // Width should span only 1 column for items
                }
            }
        });
        mListExpenses.setAdapter(adapter);
        mListExpenses.setLayoutManager(manager);
        mListExpenses.addItemDecoration(new StickyHeaderItemDecoration(adapter));

        mListExpenses.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        LiveData<List<ExpenseWrapper>> expenses = mViewModel.getExpenseWrappers();
        if (expenses != null) {
            expenses.observe(getViewLifecycleOwner(),
                    new Observer<List<ExpenseWrapper>>() {
                        @Override
                        public void onChanged(List<ExpenseWrapper> expenses) {
                            Timber.i("Refreshing expenses %s", expenses.size());
                            adapter.setExpenses(expenses);
                            onExpensesReceived(expenses);
                        }
                    });
        } else {
            Timber.i("Expenses LiveData null");
        }
    }

    private void handleExpenseDuplicateRequested(@Nonnull Expense expense) {
        mViewModel.duplicateExpense(expense)
                .observe(getViewLifecycleOwner(), new Observer<Expense>() {
                    @Override
                    public void onChanged(final Expense duplicate) {
                        Snackbar editSnackbar =
                                Snackbar.make(mBtnAdd, R.string.expenses_duplicate_success,
                                        Snackbar.LENGTH_INDEFINITE);
                        editSnackbar.setAction(R.string.common_edit, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleExpenseEditRequested(duplicate);
                            }
                        });
                        editSnackbar.show();
                    }
                });
    }

    private void handleExpensePushToRemoteRequested(@Nonnull Expense expense) {
        mViewModel.pushToRemoteShared(expense);
    }

    private void handleExpenseEditRequested(@Nonnull Expense expense) {
        AllExpensesFragmentDirections.NavActionAddExpense addAction
                = AllExpensesFragmentDirections.navActionAddExpense();
        addAction.setExpense(expense);
        NavHostFragment.findNavController(this).navigate(addAction);
    }

    private void handleExpenseDeleteRequested(@Nonnull final Expense expense) {
        if (getContext() != null) {
            DialogInterface.OnClickListener positiveListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.deleteExpense(expense);
                        }
                    };
            DialogHelper.showAlert(getContext(),
                    R.string.delete_expense_warning_message,
                    R.string.common_delete, positiveListener,
                    R.string.common_cancel, null);
        }
    }

    private void showExpenseDetails(ExpenseWrapper wrapper) {
        Timber.i("Showing information for %s", wrapper.toString());
        ExpenseDetailsFragment detailsFragment = ExpenseDetailsFragment.newInstance();
        detailsFragment.setCallback(new ExpenseDetailsFragment.DetailFragmentCallback() {
            @Override
            public void onEditRequested(@NonNull Expense expense) {
                handleExpenseEditRequested(expense);
            }

            @Override
            public void onDeleteRequested(@NonNull Expense expense) {
                handleExpenseDeleteRequested(expense);
            }

            @Override
            public void onDuplicateRequested(@Nonnull Expense expense) {
                handleExpenseDuplicateRequested(expense);
            }

            @Override
            public void onPushToRemoteSharedRequested(@Nonnull Expense expense) {
                handleExpensePushToRemoteRequested(expense);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.SELECTED_EXPENSE, wrapper.getExpense());
        bundle.putBoolean(Constants.BundleKeys.ENABLE_SHARED, mViewModel.isEnableSharedExpenses());
        detailsFragment.setArguments(bundle);
        if (getActivity() != null) {
            detailsFragment
                    .show(getActivity().getSupportFragmentManager(),
                            ExpenseDetailsFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void showFilterOptions() {
        Timber.i("Showing filter options in bottom sheet");
        FilterOptionsFragment fragment = FilterOptionsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.FILTER, mViewModel.getFilter());
        fragment.setArguments(bundle);
        fragment.setCallback(new FilterOptionsFragment.FilterOptionsFragmentCallback() {
            @Override
            public void onFilterRequested(@NonNull Filter filter) {
                onFilterApplied(filter);
            }
        });
        if (getActivity() != null) {
            fragment.show(getActivity().getSupportFragmentManager(), FilterOptionsFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void showAnalysis() {
        Timber.i("Showing analysis in bottom sheet");
        AnalysisFragment fragment = AnalysisFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.BundleKeys.ANALYSIS_EXPENSES,
                (ArrayList<? extends Parcelable>)mViewModel.getExpenses());
        fragment.setArguments(bundle);
        if (getActivity() != null) {
            fragment.show(getActivity().getSupportFragmentManager(), AnalysisFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void onFilterApplied(Filter filter) {
        mViewModel.onExpenseFilterApplied(filter);
    }

    private void onExpensesReceived(List<ExpenseWrapper> expenses) {
        boolean doCalculation = false;
        if (expenses.size() == 0) {
            mBtnAdd.setVisibility(View.GONE);
            mGroupButtons.setVisibility(View.GONE);
            mCardInfo.setVisibility(View.GONE);
            mListExpenses.setVisibility(View.GONE);

            mBtnAddSecond.setVisibility(View.VISIBLE);
            mBtnFilterSecond.setVisibility(View.VISIBLE);
            mBtnSetupSecond.setVisibility(View.VISIBLE);
            if (mViewModel.isEnableSharedExpenses()) {
                mBtnSharedSecond.setVisibility(View.VISIBLE);
            }
            mTextInfoEmpty.setVisibility(View.VISIBLE);
        } else {
            doCalculation = true;
            mBtnAdd.setVisibility(View.VISIBLE);
            mGroupButtons.setVisibility(View.VISIBLE);
            mCardInfo.setVisibility(View.VISIBLE);
            mListExpenses.setVisibility(View.VISIBLE);

            mBtnAddSecond.setVisibility(View.GONE);
            mBtnFilterSecond.setVisibility(View.GONE);
            mBtnSetupSecond.setVisibility(View.GONE);
            mBtnSharedSecond.setVisibility(View.GONE);
            mTextInfoEmpty.setVisibility(View.GONE);
        }

        // Return when no calculation required (TextInfo 1, 2, 3 are not going to be shown)
        if (!doCalculation) {
            return;
        }

        int count = mViewModel.getExpensesSize();
        BigDecimal totalAmount = mViewModel.getExpensesTotal();

        // Number of expenses
        mTextInfo2.setText(
                getResources().getQuantityString(R.plurals.expense_count_total, count, count));
        // Total
        mTextInfo3.setText(CurrencyHelper.formatForDisplay(true, totalAmount));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBtnFilter.getId() ||
                v.getId() == mBtnFilterSecond.getId()) { // Filter
            showFilterOptions();
        } else if (v.getId() == mBtnSetup.getId() ||
                v.getId() == mBtnSetupSecond.getId()) { // Setup
            NavHostFragment.findNavController(AllExpensesFragment.this)
                    .navigate(R.id.nav_action_setup, null);
        } else if (v.getId() == mBtnAdd.getId() || v.getId() == mBtnAddSecond.getId()) { // Add
            NavHostFragment.findNavController(AllExpensesFragment.this)
                    .navigate(R.id.nav_action_add_expense, null);
        } else if (v.getId() == mBtnAnalysis.getId()) { // Analysis
            showAnalysis();
        } else if (v.getId() == mBtnClearFilter.getId()) {
            mViewModel.clearFilter();
        } else if (v.getId() == mBtnShared.getId() ||
                v.getId() == mBtnSharedSecond.getId()) { // Shared get
            mViewModel.getAndSaveAndDeleteFromRemoteShared();
        }
    }
}


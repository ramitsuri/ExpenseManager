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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.IntDefs.ListItemType;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.ui.decoration.StickyHeaderItemDecoration;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.AllExpensesViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class AllExpensesFragment extends BaseFragment {

    private AllExpensesViewModel mViewModel;

    // Views
    private ExtendedFloatingActionButton mBtnAdd;
    private RecyclerView mListExpenses;
    private MaterialCardView mCardInfo;
    private TextView mTextInfoEmpty, mTextInfo1, mTextInfo2, mTextInfo3;
    private Button mBtnFilter, mBtnAnalysis, mBtnFilterSecond, mBtnSetup, mBtnSetupSecond,
            mBtnAddSecond;

    public AllExpensesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_expenses, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideActionBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(AllExpensesViewModel.class);

        mBtnAnalysis = view.findViewById(R.id.btn_analyse);
        mBtnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalysis();
            }
        });

        mBtnFilter = view.findViewById(R.id.btn_filter);
        mBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
            }
        });

        mBtnFilterSecond = view.findViewById(R.id.btn_filter_second);
        mBtnFilterSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
            }
        });

        mBtnSetup = view.findViewById(R.id.btn_setup);
        mBtnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AllExpensesFragment.this)
                        .navigate(R.id.nav_action_setup, null);
            }
        });

        mBtnSetupSecond = view.findViewById(R.id.btn_setup_second);
        mBtnSetupSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AllExpensesFragment.this)
                        .navigate(R.id.nav_action_setup, null);
            }
        });

        mBtnAdd = view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AllExpensesFragment.this)
                        .navigate(R.id.nav_action_add_expense, null);
            }
        });
        mBtnAddSecond = view.findViewById(R.id.btn_add_expense_second);
        mBtnAddSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AllExpensesFragment.this)
                        .navigate(R.id.nav_action_add_expense, null);
            }
        });

        // Shown when no expenses
        mTextInfoEmpty = view.findViewById(R.id.txt_expense_empty);

        // Shown when there are expenses
        mCardInfo = view.findViewById(R.id.card_info);
        mTextInfo1 = view.findViewById(R.id.txt_expense_info_1);
        mTextInfo2 = view.findViewById(R.id.txt_expense_info_2);
        mTextInfo3 = view.findViewById(R.id.txt_expense_info_3);

        mViewModel.getSheetName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String sheetName) {
                if (TextUtils.isEmpty(sheetName)) {
                    sheetName = getString(R.string.common_filter);
                }
                mTextInfoEmpty.setText(String.format(getString(R.string.all_expenses_empty_message),
                        sheetName));
                mTextInfo1.setText(sheetName);
            }
        });

        setupListExpenses(view);

        onSheetSelected();
    }

    private void setupListExpenses(View view) {
        mListExpenses = view.findViewById(R.id.list_expenses);
        final int numberOfColumns = getResources().getInteger(R.integer.expenses_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);

        final ExpenseAdapter adapter = new ExpenseAdapter();
        adapter.setHistorical(true);
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
                            Timber.i("Refreshing expenses" + expenses.size());
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
                                        Snackbar.LENGTH_LONG);
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
        });
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.SELECTED_EXPENSE, wrapper.getExpense());
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
        fragment.setCallback(new FilterOptionsFragment.FilterOptionsFragmentCallback() {
            @Override
            public void onFilterRequested(@NonNull SheetInfo sheetInfo) {
                if (mViewModel.getSelectedSheetId() != sheetInfo.getSheetId()) {
                    mViewModel.setSelectedSheetId(sheetInfo.getSheetId());
                    onSheetSelected();
                } else {
                    Timber.i("Same Sheet selected, ignoring request");
                }
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

    private void onSheetSelected() {
        mViewModel.refreshExpenseWrappers();
    }

    private void onExpensesReceived(List<ExpenseWrapper> expenses) {
        boolean doCalculation = false;
        if (expenses.size() == 0) {
            mBtnAnalysis.setVisibility(View.GONE);
            mBtnFilter.setVisibility(View.GONE);
            mBtnSetup.setVisibility(View.GONE);
            mCardInfo.setVisibility(View.GONE);
            mListExpenses.setVisibility(View.GONE);
            mBtnAdd.setVisibility(View.GONE);

            mBtnAddSecond.setVisibility(View.VISIBLE);
            mBtnFilterSecond.setVisibility(View.VISIBLE);
            mBtnSetupSecond.setVisibility(View.VISIBLE);
            mTextInfoEmpty.setVisibility(View.VISIBLE);
        } else {
            doCalculation = true;
            mBtnAdd.setVisibility(View.VISIBLE);
            mBtnAnalysis.setVisibility(View.VISIBLE);
            mBtnFilter.setVisibility(View.VISIBLE);
            mBtnSetup.setVisibility(View.VISIBLE);
            mCardInfo.setVisibility(View.VISIBLE);
            mListExpenses.setVisibility(View.VISIBLE);

            mBtnAddSecond.setVisibility(View.GONE);
            mBtnFilterSecond.setVisibility(View.GONE);
            mBtnSetupSecond.setVisibility(View.GONE);
            mTextInfoEmpty.setVisibility(View.GONE);
        }

        // Return when no calculation required (TextInfo 1, 2, 3 are not going to be shown)
        if (!doCalculation) {
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        int count = 0;
        int notBackedUpCount = 0;
        int startDateIndex = -1;
        int endDateIndex = -1;
        for (int i = 0; i < expenses.size(); i++) {
            /* List is in descending order of date.
             * First header encountered is end date.
             * Last header encountered is the start date
             */
            ExpenseWrapper wrapper = expenses.get(i);
            if (wrapper.getItemType() == ListItemType.ITEM) {
                count = count + 1;
                if (!wrapper.getExpense().isSynced()) {
                    notBackedUpCount = notBackedUpCount + 1;
                }
                totalAmount = totalAmount.add(expenses.get(i).getExpense().getAmount());
            } else if (wrapper.getItemType() == ListItemType.HEADER) {
                if (endDateIndex == -1) {
                    endDateIndex = i;
                }
                startDateIndex = i;
            }
        }
        // Number of expenses
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getQuantityString(R.plurals.expense_count_total, count, count));
        sb.append("\n");
        if (notBackedUpCount > 0) {
            sb.append(getString(R.string.all_expenses_count_not_backed_up, notBackedUpCount));
        } else {
            sb.append(getString(R.string.all_expenses_count_all_backed_up));
        }
        mTextInfo2.setText(sb.toString());

        // Date range
        /*if (startDateIndex == endDateIndex) {
            mTextInfo2.setText(String.format("(%1s)",
                    expenses.get(startDateIndex).getDate()));
        } else {
            mTextInfo2.setText(String.format("(%1s - %2s)",
                    expenses.get(startDateIndex).getDate(),
                    expenses.get(endDateIndex).getDate()));
        }*/

        // Total
        mTextInfo3.setText(CurrencyHelper.formatForDisplay(true, totalAmount));
    }
}

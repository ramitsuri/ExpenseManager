package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.IntDefs.ListItemType;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.ui.decoration.StickyHeaderItemDecoration;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.viewModel.AllExpensesViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class AllExpensesFragment extends BaseFragment {

    private AllExpensesViewModel mViewModel;
    private ExpenseAdapter mExpenseAdapter;

    // Views
    private RecyclerView mListExpenses;
    private MaterialCardView mCardInfo;
    private TextView mTextInfoEmpty, mTextInfo1, mTextInfo2, mTextInfo3;
    private ProgressBar mProgressBar;
    private Button mBtnFilter, mBtnAnalysis, mBtnFilterSecond;

    public AllExpensesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_expenses, container, false);
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

        mViewModel = ViewModelProviders.of(this).get(AllExpensesViewModel.class);

        LiveData<List<SheetInfo>> sheetInfos = mViewModel.getSheetInfosLiveData();
        if (sheetInfos != null) {
            sheetInfos.observe(getViewLifecycleOwner(),
                    new Observer<List<SheetInfo>>() {
                        @Override
                        public void onChanged(List<SheetInfo> sheetInfos) {
                            onSheetInfosReceived(sheetInfos);
                        }
                    });
        }

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
                if (mViewModel.getSheetInfos() != null) {
                    showFilterOptions(new ArrayList<>(mViewModel.getSheetInfos()));
                }
            }
        });

        mBtnFilterSecond = view.findViewById(R.id.btn_filter_second);
        mBtnFilterSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.getSheetInfos() != null) {
                    showFilterOptions(new ArrayList<>(mViewModel.getSheetInfos()));
                }
            }
        });

        // Shown when no expenses
        mTextInfoEmpty = view.findViewById(R.id.txt_expense_empty);

        // Shown when there are expenses
        mCardInfo = view.findViewById(R.id.card_info);
        mTextInfo1 = view.findViewById(R.id.txt_expense_info_1);
        mTextInfo2 = view.findViewById(R.id.txt_expense_info_2);
        mTextInfo3 = view.findViewById(R.id.txt_expense_info_3);

        mProgressBar = view.findViewById(R.id.progress);

        setupListExpenses(view);
    }

    private void setupListExpenses(View view) {
        mListExpenses = view.findViewById(R.id.list_expenses);
        final int numberOfColumns = getResources().getInteger(R.integer.expenses_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);

        mExpenseAdapter = new ExpenseAdapter();
        mExpenseAdapter.setHistorical(true);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(
                    int position) { // Set headers to span the whole width of recycler view
                @ListItemType
                int itemType = mExpenseAdapter.getItemViewType(position);
                switch (itemType) {
                    case ListItemType.HEADER:
                        return numberOfColumns;

                    case ListItemType.ITEM:

                    default:
                        return 1; // Width should span only 1 column for items
                }
            }
        });
        mListExpenses.setAdapter(mExpenseAdapter);
        mListExpenses.setLayoutManager(manager);
        mListExpenses.addItemDecoration(new StickyHeaderItemDecoration(mExpenseAdapter));

        mExpenseAdapter.setCallback(new ExpenseAdapter.ItemClickListener() {
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
                // Not used
            }

            @Override
            public void onDeleteRequested(@NonNull Expense expense) {
                // Not used
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
                    .show(getActivity().getSupportFragmentManager(), ExpenseDetailsFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void handleExpenseDuplicateRequested(@Nonnull Expense expense) {
        mViewModel.duplicateExpense(expense)
                .observe(getViewLifecycleOwner(), new Observer<Expense>() {
                    @Override
                    public void onChanged(final Expense duplicate) {
                        Snackbar editSnackbar =
                                Snackbar.make(mListExpenses, R.string.expenses_duplicate_success,
                                        Snackbar.LENGTH_LONG);
                        editSnackbar.show();
                    }
                });
    }

    private void showFilterOptions(ArrayList<SheetInfo> sheetInfos) {
        Timber.i("Showing filter options in bottom sheet");
        FilterOptionsFragment fragment = FilterOptionsFragment.newInstance();
        fragment.setCallback(new FilterOptionsFragment.FilterOptionsFragmentCallback() {
            @Override
            public void onFilterRequested(@NonNull SheetInfo sheetInfo) {
                onSheetSelected(sheetInfo, false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.BundleKeys.SHEET_INFOS, sheetInfos);
        bundle.putInt(Constants.BundleKeys.SELECTED_SHEET_ID, AppHelper.getDefaultSheetId());
        fragment.setArguments(bundle);
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
                (ArrayList<? extends Parcelable>)mExpenseAdapter.getExpenses());
        fragment.setArguments(bundle);
        if (getActivity() != null) {
            fragment.show(getActivity().getSupportFragmentManager(), AnalysisFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void onSheetInfosReceived(final List<SheetInfo> sheetInfos) {
        Timber.i("Sheetinfos received");
        if (sheetInfos != null) {
            for (SheetInfo sheetInfo : sheetInfos) {
                if (mViewModel.getSelectedSheetId() == sheetInfo.getSheetId()) {
                    Timber.i("Selecting default sheet id");
                    onSheetSelected(sheetInfo, true);
                    break;
                }
            }
        }
    }

    private void onSheetSelected(@Nonnull SheetInfo sheetInfo, boolean isFirstTime) {
        Timber.i("Sheet selected - %s", sheetInfo.getSheetName());
        if (mViewModel.getSelectedSheetId() != sheetInfo.getSheetId() || isFirstTime) {
            mListExpenses.setVisibility(View.GONE);
            mCardInfo.setVisibility(View.GONE);
            mTextInfoEmpty.setVisibility(View.GONE);
            mBtnFilterSecond.setVisibility(View.GONE);
            mBtnFilter.setVisibility(View.GONE);
            mBtnAnalysis.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mViewModel.setSelectedSheetId(sheetInfo.getSheetId());
            LiveData<List<ExpenseWrapper>> expenses = mViewModel.getExpenses(sheetInfo);
            if (expenses != null) {
                expenses.observe(getViewLifecycleOwner(),
                        new Observer<List<ExpenseWrapper>>() {
                            @Override
                            public void onChanged(List<ExpenseWrapper> expenses) {
                                Timber.i("Refreshing expenses");
                                mExpenseAdapter.setExpenses(expenses);
                                onExpensesReceived(expenses);
                            }
                        });
            } else {
                Timber.i("Expenses null");
            }
        } else {
            Timber.i("Same Sheet selected, ignoring request");
        }
    }

    private void onExpensesReceived(List<ExpenseWrapper> expenses) {
        boolean doCalculation = false;
        mProgressBar.setVisibility(View.GONE);
        if (expenses.size() == 0) {
            mBtnFilterSecond.setVisibility(View.VISIBLE);
            mTextInfoEmpty.setVisibility(View.VISIBLE);
            mTextInfoEmpty.setText(String.format(getString(R.string.all_expenses_empty_message),
                    mViewModel.getSelectedSheetName()));
            mCardInfo.setVisibility(View.GONE);
            mBtnAnalysis.setVisibility(View.GONE);
            mBtnFilter.setVisibility(View.GONE);
            mListExpenses.setVisibility(View.GONE);
        } else {
            doCalculation = true;
            mBtnFilterSecond.setVisibility(View.GONE);
            mTextInfoEmpty.setVisibility(View.GONE);
            mCardInfo.setVisibility(View.VISIBLE);
            mBtnAnalysis.setVisibility(View.VISIBLE);
            mBtnFilter.setVisibility(View.VISIBLE);
            mListExpenses.setVisibility(View.VISIBLE);
        }

        // Return when no calculation required (TextInfo 1, 2, 3 are not going to be shown)
        if (!doCalculation) {
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        int count = 0;
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
                totalAmount = totalAmount.add(expenses.get(i).getExpense().getAmount());
            } else if (wrapper.getItemType() == ListItemType.HEADER) {
                if (endDateIndex == -1) {
                    endDateIndex = i;
                }
                startDateIndex = i;
            }
        }
        // Number of expenses
        String sheetName = mViewModel.getSelectedSheetName();
        if (!TextUtils.isEmpty(sheetName)) {
            mTextInfo1.setText(getResources()
                    .getQuantityString(R.plurals.backed_up_expense_count_in_sheet, count, count,
                            sheetName));
        } else {
            mTextInfo1.setText(getResources()
                    .getQuantityString(R.plurals.backed_up_expense_count, count, count));
        }

        // Date range
        if (startDateIndex == endDateIndex) {
            mTextInfo2.setText(String.format("(%1s)",
                    expenses.get(startDateIndex).getDate()));
        } else {
            mTextInfo2.setText(String.format("(%1s - %2s)",
                    expenses.get(startDateIndex).getDate(),
                    expenses.get(endDateIndex).getDate()));
        }

        // Total
        mTextInfo3.setText(CurrencyHelper.formatForDisplay(true, totalAmount));
    }
}

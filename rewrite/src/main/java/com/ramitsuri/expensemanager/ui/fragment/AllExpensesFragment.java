package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class AllExpensesFragment extends BaseFragment {

    private AllExpensesViewModel mViewModel;
    private ExpenseAdapter mExpenseAdapter;

    // Views
    private ExtendedFloatingActionButton mBtnSelectSheet;
    private MaterialCardView mCardInfo;
    private TextView mTextInfoEmpty, mTextInfo1, mTextInfo2, mTextInfo3;

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

        mBtnSelectSheet = view.findViewById(R.id.btn_select_sheet);
        mBtnSelectSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel.getSheetInfos() != null) {
                    showSheets(new ArrayList<>(mViewModel.getSheetInfos()));
                }
            }
        });

        mViewModel.getSheetInfosLiveData().observe(getViewLifecycleOwner(),
                new Observer<List<SheetInfo>>() {
                    @Override
                    public void onChanged(List<SheetInfo> sheetInfos) {
                        onSheetInfosReceived(sheetInfos);
                    }
                });

        // Shown when no expenses
        mTextInfoEmpty = view.findViewById(R.id.txt_expense_empty);

        // Shown when there are expenses
        mCardInfo = view.findViewById(R.id.card_info);
        mTextInfo1 = view.findViewById(R.id.txt_expense_info_1);
        mTextInfo2 = view.findViewById(R.id.txt_expense_info_2);
        mTextInfo3 = view.findViewById(R.id.txt_expense_info_3);

        setupListExpenses(view);
    }

    private void setupListExpenses(View view) {
        final RecyclerView listExpenses = view.findViewById(R.id.list_expenses);
        final int numberOfColumns = getResources().getInteger(R.integer.expenses_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);

        mExpenseAdapter = new ExpenseAdapter();
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
        listExpenses.setAdapter(mExpenseAdapter);
        listExpenses.setLayoutManager(manager);
        listExpenses.addItemDecoration(new StickyHeaderItemDecoration(mExpenseAdapter));
        listExpenses.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mBtnSelectSheet.hide();
                } else if (dy < 0) {
                    mBtnSelectSheet.show();
                }
            }
        });

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
                                Snackbar.make(mBtnSelectSheet, R.string.expenses_duplicate_success,
                                        Snackbar.LENGTH_LONG);
                        editSnackbar.show();
                    }
                });
    }

    private void showSheets(ArrayList<SheetInfo> sheetInfos) {
        Timber.i("Showing sheets in bottom sheet");
        ChangeSheetFragment fragment = ChangeSheetFragment.newInstance();
        fragment.setCallback(new ChangeSheetFragment.ChangeSheetFragmentCallback() {
            @Override
            public void onChangeSheetRequested(@NonNull SheetInfo sheetInfo) {
                onSheetSelected(sheetInfo, false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.BundleKeys.SHEET_INFOS, sheetInfos);
        bundle.putInt(Constants.BundleKeys.SELECTED_SHEET_ID, AppHelper.getDefaultSheetId());
        fragment.setArguments(bundle);
        if (getActivity() != null) {
            fragment.show(getActivity().getSupportFragmentManager(), ChangeSheetFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }

    private void onSheetInfosReceived(final List<SheetInfo> sheetInfos) {
        Timber.i("Sheetinfos received");
        if (mViewModel.getSheetInfos() != null && mViewModel.getSheetInfos().size() >= 1) {
            for (SheetInfo sheetInfo : mViewModel.getSheetInfos()) {
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
            mViewModel.setSelectedSheetId(sheetInfo.getSheetId());
            mViewModel.getExpenses(sheetInfo).observe(getViewLifecycleOwner(),
                    new Observer<List<ExpenseWrapper>>() {
                        @Override
                        public void onChanged(List<ExpenseWrapper> expenses) {
                            Timber.i("Refreshing expenses");
                            mExpenseAdapter.setExpenses(expenses);
                            setTextInfo(expenses);
                        }
                    });
        } else {
            Timber.i("Same Sheet selected, ignoring request");
        }
    }

    private void setTextInfo(List<ExpenseWrapper> expenses) {
        boolean doCalculation = false;
        if (expenses.size() == 0) {
            mTextInfoEmpty.setVisibility(View.VISIBLE);
            mCardInfo.setVisibility(View.GONE);
        } else {
            doCalculation = true;
            mTextInfoEmpty.setVisibility(View.GONE);
            mCardInfo.setVisibility(View.VISIBLE);
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

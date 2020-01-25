package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.ramitsuri.expensemanager.IntDefs.ListItemType;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.viewModel.AllExpensesViewModel;

import java.util.ArrayList;
import java.util.List;

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
    private AutoCompleteTextView mViewDropDown;
    private TextView mTextInfoEmpty;
    private ExpenseAdapter mExpenseAdapter;

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

        mViewDropDown = view.findViewById(R.id.spinner_sheet_value);
        mViewDropDown.setKeyListener(null);

        mViewModel.getSheetInfos().observe(getViewLifecycleOwner(),
                new Observer<List<SheetInfo>>() {
                    @Override
                    public void onChanged(List<SheetInfo> sheetInfos) {
                        onSheetInfosReceived(sheetInfos);
                    }
                });

        // Shown when no expenses
        mTextInfoEmpty = view.findViewById(R.id.txt_expense_empty);

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
    }

    private void onSheetInfosReceived(final List<SheetInfo> sheetInfos) {
        Timber.i("Sheetinfos received");
        final List<String> sheets = new ArrayList<>();
        for (SheetInfo info : sheetInfos) {
            sheets.add(info.getSheetName());
        }
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.spinner_item);
        adapter.addAll(sheets);
        mViewDropDown.setAdapter(adapter);
        mViewDropDown.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
                        onSheetSelected(sheetInfos.get(position));
                    }
                });
    }

    private void onSheetSelected(SheetInfo sheetInfo) {
        mViewModel.getExpenses(sheetInfo).observe(getViewLifecycleOwner(),
                new Observer<List<ExpenseWrapper>>() {
                    @Override
                    public void onChanged(List<ExpenseWrapper> expenses) {
                        Timber.i("Refreshing expenses");
                        mExpenseAdapter.setExpenses(expenses);
                    }
                });
    }
}

package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.ui.adapter.BudgetCategoryWrapper;
import com.ramitsuri.expensemanager.ui.adapter.ListSelectionItemAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class AddBudgetFragment extends BaseBottomSheetFragment {

    static final String TAG = AddBudgetFragment.class.getName();

    static AddBudgetFragment newInstance() {
        return new AddBudgetFragment();
    }

    private AddBudgetCallback mCallback;

    public interface AddBudgetCallback {
        void onChanged(@NonNull Budget budget);
    }

    public void setCallback(@NonNull AddBudgetCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_budget, container, false);
        setSystemUiVisibility(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Budget budget = getArguments().getParcelable(Constants.BundleKeys.SELECTED_BUDGET);
            if (budget == null) {
                budget = new Budget();
            }
            List<BudgetCategoryWrapper> categories =
                    getArguments().getParcelableArrayList(Constants.BundleKeys.ALL_CATEGORIES);
            if (categories != null) {
                setupViews(view, budget, categories);
            }
        }
    }

    private void setupViews(@NonNull View view, @Nonnull final Budget budget,
            @Nonnull final List<BudgetCategoryWrapper> categories) {
        // Name
        final EditText editName = view.findViewById(R.id.edit_text_name);
        if (budget.getName() != null) {
            editName.setText(budget.getName());
        }
        editName.setSelection(editName.getText().toString().length());

        // Amount
        final EditText editAmount = view.findViewById(R.id.edit_text_amount);
        if (budget.getAmount() != null) {
            String value = budget.getAmount().toString();
            editAmount.setText(value);
        }
        editAmount.setSelection(editAmount.getText().toString().length());

        // Error message
        final TextView txtError = view.findViewById(R.id.text_categories_error);

        // Categories
        RecyclerView listCategories = view.findViewById(R.id.list_categories);
        listCategories.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL));
        listCategories.setHasFixedSize(true);
        final ListSelectionItemAdapter adapter = new ListSelectionItemAdapter();
        Collections.sort(categories, new SortCategories());
        adapter.setValues(categories);
        adapter.setCallback(new ListSelectionItemAdapter.ListSelectionItemCallback() {
            @Override
            public void onItemSelected(BudgetCategoryWrapper value) {
                BudgetCategoryWrapper newValue = new BudgetCategoryWrapper(value.getCategory());
                newValue.setSelected(true);
                newValue.setAvailable(true);
                int index = categories.indexOf(value);
                categories.remove(value);
                categories.add(index, newValue);
                Collections.sort(categories, new SortCategories());
                adapter.setValues(categories);
                txtError.setVisibility(View.GONE);
            }

            @Override
            public void onItemUnselected(BudgetCategoryWrapper value) {
                BudgetCategoryWrapper newValue = new BudgetCategoryWrapper(value.getCategory());
                newValue.setSelected(false);
                newValue.setAvailable(true);
                int index = categories.indexOf(value);
                categories.remove(value);
                categories.add(index, newValue);
                Collections.sort(categories, new SortCategories());
                adapter.setValues(categories);
                txtError.setVisibility(View.GONE);
            }
        });
        listCategories.setAdapter(adapter);

        // Done button
        final Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> selectedCategories = new ArrayList<>();
                for (BudgetCategoryWrapper wrapper : categories) {
                    if (wrapper.isSelected()) {
                        selectedCategories.add(wrapper.getCategory());
                    }
                }
                if (selectedCategories.size() == 0) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(R.string.setup_budget_select_one_category);
                    return;
                }
                if (selectedCategories.size() > Constants.Basic.BUDGET_CATEGORY_COUNT) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(R.string.setup_budget_selected_max_category);
                    return;
                }
                String name = editName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    name = getString(R.string.setup_budget_default_name);
                }
                if (mCallback != null) {
                    budget.setName(name);
                    budget.setAmount(new BigDecimal(editAmount.getText().toString().trim()));
                    budget.setCategories(selectedCategories);
                    dismiss();
                    mCallback.onChanged(budget);
                }
            }
        });
    }

    static class SortCategories implements Comparator<BudgetCategoryWrapper> {
        @Override
        public int compare(BudgetCategoryWrapper o1, BudgetCategoryWrapper o2) {
            if (o1.isAvailable() && !o2.isAvailable()) {
                return -1;
            }
            if (!o1.isAvailable() && o2.isAvailable()) {
                return 1;
            }
            return 0;
        }
    }
}

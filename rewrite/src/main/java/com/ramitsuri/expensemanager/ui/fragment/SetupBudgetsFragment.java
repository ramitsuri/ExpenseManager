package com.ramitsuri.expensemanager.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.ui.adapter.BudgetCategoryWrapper;
import com.ramitsuri.expensemanager.ui.adapter.BudgetSetupAdapter;
import com.ramitsuri.expensemanager.viewModel.SetupBudgetsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SetupBudgetsFragment extends BaseFragment {

    private SetupBudgetsViewModel mViewModel;

    public SetupBudgetsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setup_budgets, container, false);
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

        mViewModel = new ViewModelProvider(this).get(SetupBudgetsViewModel.class);

        mViewModel.areCategoriesLoaded().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loaded) {
                Timber.i("Categories loaded - %s", loaded);
            }
        });
        setupViews(view);
    }

    private void setupViews(@Nonnull View view) {
        // Close
        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });

        // Done
        Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.save();
                exitToUp();
            }
        });

        // Add new
        final Button btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("Add requested");
                ArrayList<BudgetCategoryWrapper> categoryWrappers =
                        mViewModel.getCategoryWrappers(null);
                if (categoryWrappers == null) {
                    Snackbar.make(btnAdd, R.string.setup_budget_categories_utilized,
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                showBudgetEdit(null, categoryWrappers);
            }
        });

        // Views
        RecyclerView listItems = view.findViewById(R.id.list_items);
        if (getContext() != null) {
            DividerItemDecoration divider =
                    new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            Drawable dividerDrawable =
                    ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
            if (dividerDrawable != null) {
                divider.setDrawable(dividerDrawable);
                listItems.addItemDecoration(divider);
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listItems.setLayoutManager(manager);
        final BudgetSetupAdapter adapter = new BudgetSetupAdapter();
        adapter.setCallback(
                new BudgetSetupAdapter.BudgetSetupAdapterCallback() {
                    @Override
                    public void onItemDeleteRequested(@Nonnull Budget value) {
                        Timber.i("Delete requested: %s", value);
                        if (mViewModel.delete(value)) {
                            Timber.i("Delete succeeded");
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.setup_budget_deleted,
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Timber.i("Delete failed");
                        }
                    }

                    @Override
                    public void onItemEditRequested(@Nonnull Budget value) {
                        Timber.i("Edit requested %s", value);
                        ArrayList<BudgetCategoryWrapper> categoryWrappers =
                                mViewModel.getCategoryWrappers(value);
                        if (categoryWrappers == null) {
                            Snackbar.make(btnAdd, R.string.setup_budget_categories_utilized,
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        showBudgetEdit(value, categoryWrappers);
                    }
                });
        listItems.setAdapter(adapter);
        mViewModel.getValuesLive()
                .observe(getViewLifecycleOwner(), new Observer<List<Budget>>() {
                    @Override
                    public void onChanged(List<Budget> budgets) {
                        adapter.setValues(budgets);
                    }
                });
    }

    private void showBudgetEdit(@Nullable final Budget value,
            ArrayList<BudgetCategoryWrapper> categoryWrappers) {
        Timber.i("Showing budget in bottom sheet");
        AddBudgetFragment fragment = AddBudgetFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.SELECTED_BUDGET, value);
        bundle.putParcelableArrayList(Constants.BundleKeys.ALL_CATEGORIES, categoryWrappers);
        fragment.setArguments(bundle);
        fragment.setCallback(new AddBudgetFragment.AddBudgetCallback() {
            @Override
            public void onChanged(@NonNull Budget newValue) {
                if (value == null) { // Add new
                    if (mViewModel.add(newValue)) {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_budget_added,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        Timber.i("Add succeeded");
                    } else {
                        Timber.i("Add failed");
                    }
                } else { // Edit
                    if (mViewModel.edit(value, newValue)) {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_budget_edited,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        Timber.i("Edit succeeded");
                    } else {
                        Timber.i("Edit failed");
                    }
                }
            }
        });

        if (getActivity() != null) {
            fragment.show(getActivity().getSupportFragmentManager(), AddBudgetFragment.TAG);
        } else {
            Timber.e("getActivity() returned null when showing details fragment");
        }
    }
}

package com.ramitsuri.expensemanager.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.ui.adapter.BudgetSetupAdapter;
import com.ramitsuri.expensemanager.ui.adapter.BudgetSetupWrapper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.SetupBudgetsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SetupBudgetsFragment extends BaseFragment {

    private SetupBudgetsViewModel mViewModel;

    // Views
    private RecyclerView mListItems;

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

        Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });

        mListItems = view.findViewById(R.id.list_items);
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
        mListItems.setLayoutManager(manager1);
        final BudgetSetupAdapter adapter = new BudgetSetupAdapter();

        mListItems.setAdapter(adapter);
        mViewModel.getBudgets()
                .observe(getViewLifecycleOwner(), new Observer<List<Budget>>() {
                    @Override
                    public void onChanged(List<Budget> budgets) {
                        List<BudgetSetupWrapper> wrappers = new ArrayList<>();
                        for (Budget budget : budgets) {
                            wrappers.add(new BudgetSetupWrapper(budget));
                        }
                        adapter.setValues(wrappers);
                    }
                });
    }

    private void addBudget(String newValue) {
        Timber.i("Add new budget %s", newValue);
    }

    private void showAddEntityDialog() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        final EditText input = new EditText(context);
        DialogInterface.OnClickListener positiveListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(input.getText().toString())) {
                            return;
                        }
                        addBudget(input.getText().toString());
                    }
                };

        DialogHelper.showAlertWithInput(context,
                input,
                R.string.setup_add_new,
                R.string.common_ok, positiveListener,
                R.string.common_cancel, null);
    }
}

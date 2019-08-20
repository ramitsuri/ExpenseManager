package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanagerrewrite.viewModel.ExpensesViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ExpensesFragment extends BaseFragment {

    private ExpensesViewModel mExpensesViewModel;

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

        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ExpensesFragment.this)
                        .navigate(R.id.nav_action_add_expense, null);
            }
        });

        setupListExpenses(view);
    }

    private void setupListExpenses(View view) {
        final RecyclerView listExpenses = view.findViewById(R.id.list_expenses);
        listExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ExpenseAdapter adapter = new ExpenseAdapter();
        listExpenses.setAdapter(adapter);
        mExpensesViewModel.getExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                Timber.i("Refreshing expenses");
                adapter.setExpenses(expenses);
            }
        });
    }
}

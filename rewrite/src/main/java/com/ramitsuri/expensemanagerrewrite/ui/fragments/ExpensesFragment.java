package com.ramitsuri.expensemanagerrewrite.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.data.DummyData;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.ui.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanagerrewrite.viewModel.ExpenseViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpensesFragment extends Fragment {

    private ExpenseViewModel mExpenseViewModel;

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

        mExpenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);

        setupListExpenses(view);

        /*        NavHostFragment.findNavController(ExpensesFragment.this)
                        .navigate(R.id.nav_action_add_expense, null);*/

    }

    private void setupListExpenses(View view) {
        final RecyclerView listExpenses = view.findViewById(R.id.list_expenses);
        listExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ExpenseAdapter adapter = new ExpenseAdapter();
        listExpenses.setAdapter(adapter);
        mExpenseViewModel.getExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                adapter.setExpenses(expenses);
            }
        });
    }
}

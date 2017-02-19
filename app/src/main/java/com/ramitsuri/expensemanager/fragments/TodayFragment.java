package com.ramitsuri.expensemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.ArrayList;

/**
 * Created by ramitsuri on 1/21/2017.
 */

public class TodayFragment extends Fragment {

    private ArrayList<Expense> mExpenses;
    private ExpenseAdapter mExpenseAdapter;

    private AllFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        RecyclerView recyclerViewExpenses = (RecyclerView)view.findViewById(R.id.recycler_view_expenses);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getActivity());
        mExpenses = getExpenses(Constants.TAB_ALL_ID);
        mExpenseAdapter = new ExpenseAdapter(mExpenses);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(recyclerViewLManager);
        recyclerViewExpenses.setAdapter(mExpenseAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.recycler_view_divider));
        recyclerViewExpenses.addItemDecoration(divider);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AllFragment.OnFragmentInteractionListener) {
            mListener = (AllFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ArrayList<Expense> getExpenses(int tabId) {
        ArrayList<Expense> expenses = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Expense expense = new Expense();
            expense.setCategory(new Category(1, "Food", 2));
            expense.setPaymentMode("Discover");
            expense.setDescription("Curd");
            expense.setAmount(0);
            expenses.add(expense);
        }
        return expenses;
    }

}

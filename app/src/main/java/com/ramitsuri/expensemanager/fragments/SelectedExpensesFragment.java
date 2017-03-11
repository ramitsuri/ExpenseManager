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
import android.widget.TextView;
import android.widget.Toast;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.db.ExpenseHelper;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.helper.DateHelper;

import java.util.List;

public class SelectedExpensesFragment extends Fragment {

    private List<Expense> mExpenses;
    private ExpenseAdapter mExpenseAdapter;
    private TextView mFieldTopDate;
    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(Others.EXPENSE_VIEW_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_expenses, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view){
        mFieldTopDate = (TextView)view.findViewById(R.id.date);
        mFieldTopDate.setText(DateHelper.getTodaysDate());
        RecyclerView recyclerViewExpenses =
                (RecyclerView)view.findViewById(R.id.recycler_view_expenses);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getActivity());
        mExpenses = getExpenses(0);
        mExpenseAdapter = new ExpenseAdapter(mExpenses);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(recyclerViewLManager);
        recyclerViewExpenses.setAdapter(mExpenseAdapter);
        DividerItemDecoration divider =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.recycler_view_divider));
        //recyclerViewExpenses.addItemDecoration(divider);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume(){
        super.onResume();
        mExpenseAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private List<Expense> getExpenses(int tabId) {
        List<Expense> expenses = ExpenseHelper.getExpenses();
        /*for (int i=0; i<10; i++) {
            Expense expense = new Expense();
            expense.setCategoryId(new Category(1, "Food", 2));
            expense.setPaymentModeId("Discover");
            expense.setDescription("Curd");
            expense.setAmount("0");
            expenses.add(expense);
        }*/
        return expenses;
    }

}

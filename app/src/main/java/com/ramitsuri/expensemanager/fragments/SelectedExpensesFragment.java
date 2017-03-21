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

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.helper.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectedExpensesFragment extends Fragment {

    private List<Expense> mExpenses;
    private ExpenseAdapter mExpenseAdapter;
    private TextView mFieldTopDate, mTotal, mTopExpense;
    private ExpenseWrapper mExpenseWrapper;
    private int mType;
    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(Others.EXPENSE_VIEW_TYPE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_selected_expenses, container, false);
        return mMainView;
    }

    private void setupViews(){
        mFieldTopDate = (TextView)mMainView.findViewById(R.id.date);
        mFieldTopDate.setText(mExpenseWrapper.getDate());
        mTotal = (TextView)mMainView.findViewById(R.id.expense_total);
        mTotal.setText(AppHelper.getCurrency().split("-")[1] + mExpenseWrapper.getTotal());
        /*mTopExpense = (TextView) mMainView.findViewById(R.id.value_top_expense);
        mTopExpense.setText(mExpenseWrapper.getTopExpense());*/

        RecyclerView recyclerViewExpenses =
                (RecyclerView)mMainView.findViewById(R.id.recycler_view_expenses);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getActivity());

        mExpenses = mExpenseWrapper.getExpenses();
        if(mExpenses == null){
            mExpenses = new ArrayList<>();
        }
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
        if(mExpenseWrapper == null) {
            mExpenseWrapper = getExpenseWrapper();
        }
        //mExpenseAdapter.notifyDataSetChanged();
        setupViews();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ExpenseWrapper getExpenseWrapper(){
        return ExpenseHelper.getExpenseWrapper(mType);
    }

}

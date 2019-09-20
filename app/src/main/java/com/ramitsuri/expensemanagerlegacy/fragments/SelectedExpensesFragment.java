package com.ramitsuri.expensemanagerlegacy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanagerlegacy.constants.Others;
import com.ramitsuri.expensemanagerlegacy.entities.Expense;
import com.ramitsuri.expensemanagerlegacy.entities.ExpenseWrapper;
import com.ramitsuri.expensemanagerlegacy.helper.AppHelper;
import com.ramitsuri.expensemanagerlegacy.helper.ExpenseHelper;

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

    private void setupViews() {
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
        if (mExpenses == null) {
            mExpenses = new ArrayList<>();
        }
        mExpenseAdapter = new ExpenseAdapter(mExpenses, getActivity());
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
    public void onResume() {
        super.onResume();
        mExpenseWrapper = getExpenseWrapper();
        //mExpenseAdapter.notifyDataSetChanged();
        setupViews();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ExpenseWrapper getExpenseWrapper() {
        return ExpenseHelper.getExpenseWrapper(mType);
    }
}

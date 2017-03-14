package com.ramitsuri.expensemanager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;

import java.util.List;

public class AllExpensesActivity extends AppCompatActivity {

    private List<Expense> mExpenses;
    private ExpenseAdapter mExpenseAdapter;
    private ExpenseWrapper mExpenseWrapper;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);

        mExpenseWrapper = getExpenseWrapper();
        setupActionBar();
        setupViews();
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews(){
        RecyclerView recyclerViewExpenses =
                (RecyclerView)findViewById(R.id.recycler_view_expenses);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(this);
        mExpenses = mExpenseWrapper.getExpenses();
        mExpenseAdapter = new ExpenseAdapter(mExpenses);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(recyclerViewLManager);
        recyclerViewExpenses.setAdapter(mExpenseAdapter);
    }

    private ExpenseWrapper getExpenseWrapper(){
        return ExpenseHelper.getExpenseWrapper(ExpenseViewType.ALL);
    }
}

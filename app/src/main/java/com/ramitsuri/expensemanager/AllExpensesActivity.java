package com.ramitsuri.expensemanager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.util.ArrayList;
import java.util.List;

public class AllExpensesActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private List<Expense> mExpenses;
    private ExpenseAdapter mExpenseAdapter;
    private ExpenseWrapper mExpenseWrapper;
    private Toolbar mToolbar;
    private Spinner mCategorySpinner, mPaymentMethodSpinner;

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
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_categories);
        mCategorySpinner.setOnItemSelectedListener(this);
        mPaymentMethodSpinner = (Spinner) findViewById(R.id.spinner_payment_methods);
        mPaymentMethodSpinner.setOnItemSelectedListener(this);

        List<Category> categories = CategoryHelper.getAllCategories();
        ArrayAdapter<Category> categoriesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoriesAdapter);

        List<PaymentMethod> paymentMethods = PaymentMethodHelper.getAllPaymentMethods();
        ArrayAdapter<PaymentMethod> paymentMethodsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentMethodsAdapter.
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPaymentMethodSpinner.setAdapter(paymentMethodsAdapter);
    }

    private ExpenseWrapper getExpenseWrapper(){
        return ExpenseHelper.getExpenseWrapper(ExpenseViewType.ALL);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int a = i;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

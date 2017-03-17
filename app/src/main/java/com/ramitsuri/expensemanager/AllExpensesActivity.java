package com.ramitsuri.expensemanager;

import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.dialog.DatePickerDialogFragment;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.util.ArrayList;
import java.util.List;

public class AllExpensesActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, DatePickerDialogFragment.DatePickerCallbacks,
        View.OnClickListener{

    private ExpenseAdapter mExpenseAdapter;
    private ExpenseWrapper mExpenseWrapper;
    private Toolbar mToolbar;
    private Spinner mCategorySpinner, mPaymentMethodSpinner;
    private TextView mTotal;
    private ArrayAdapter<Category> mCategoriesAdapter;
    private List<Category> mCategories;
    private List<PaymentMethod> mPaymentMethods;
    private ArrayAdapter<PaymentMethod> mPaymentMethodsAdapter;
    private TextView mDatePicker1, mDatePicker2;

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
        mDatePicker1 = (TextView) findViewById(R.id.search_date1);
        mDatePicker1.setOnClickListener(this);
        mDatePicker2 = (TextView) findViewById(R.id.search_date2);
        mDatePicker2.setOnClickListener(this);
        RecyclerView recyclerViewExpenses =
                (RecyclerView)findViewById(R.id.recycler_view_expenses);
        mTotal = (TextView) findViewById(R.id.expense_total);
        mTotal.setText(AppHelper.getCurrency() + mExpenseWrapper.getTotal());

        mExpenseAdapter = new ExpenseAdapter(mExpenseWrapper.getExpenses());
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(this);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(recyclerViewLManager);
        recyclerViewExpenses.setAdapter(mExpenseAdapter);

        mCategorySpinner = (Spinner) findViewById(R.id.spinner_categories);
        mCategorySpinner.setOnItemSelectedListener(this);
        mPaymentMethodSpinner = (Spinner) findViewById(R.id.spinner_payment_methods);
        mPaymentMethodSpinner.setOnItemSelectedListener(this);

        mCategories = CategoryHelper.getAllCategories();
        mCategoriesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mCategories);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(mCategoriesAdapter);

        mPaymentMethods = PaymentMethodHelper.getAllPaymentMethods();
        mPaymentMethodsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mPaymentMethods);
        mPaymentMethodsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        mPaymentMethodSpinner.setAdapter(mPaymentMethodsAdapter);
    }

    private ExpenseWrapper getExpenseWrapper(){
        return ExpenseHelper.getExpenseWrapper(ExpenseViewType.ALL);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == mCategorySpinner){

        } else if(adapterView == mPaymentMethodSpinner) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDatePicked(int year, int month, int day) {

    }

    @Override
    public void onClick(View view) {
        if(view == mDatePicker1){
            mDatePicker1.setSelected(true);
            mDatePicker2.setSelected(false);
            showDatePicker();
        } else if(view == mDatePicker2){
            mDatePicker1.setSelected(false);
            mDatePicker2.setSelected(true);
            showDatePicker();
        }
    }

    private void showDatePicker() {
        DialogFragment newFragment = DatePickerDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), DatePickerDialogFragment.TAG);
    }
}

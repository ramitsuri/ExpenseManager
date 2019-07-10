package com.ramitsuri.expensemanager.ui;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.adapter.ExpenseAdapter;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.dialog.DatePickerDialogFragment;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.DateHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.util.Calendar;
import java.util.List;

public class AllExpensesActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, DatePickerDialogFragment.DatePickerCallbacks,
        View.OnClickListener {

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
    private int mYear1, mYear2, mMonth1, mMonth2, mDay1, mDay2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);

        mExpenseWrapper = getExpenseWrapper();
        setupActionBar();
        setupViews();
        setupDate();
    }

    private void setupDate() {
        Calendar calendar = Calendar.getInstance();
        handleDatePicked(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), true);

        calendar.add(Calendar.DATE, 30);

        handleDatePicked(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
    }

    private void setupActionBar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews() {
        mDatePicker1 = (TextView)findViewById(R.id.search_date1);
        mDatePicker1.setOnClickListener(this);
        mDatePicker2 = (TextView)findViewById(R.id.search_date2);
        mDatePicker2.setOnClickListener(this);
        RecyclerView recyclerViewExpenses =
                (RecyclerView)findViewById(R.id.recycler_view_expenses);
        mTotal = (TextView)findViewById(R.id.expense_total);
        mTotal.setText(AppHelper.getCurrency().split("-")[1] + mExpenseWrapper.getTotal());

        mExpenseAdapter = new ExpenseAdapter(mExpenseWrapper.getExpenses());
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(this);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(recyclerViewLManager);
        recyclerViewExpenses.setAdapter(mExpenseAdapter);

        mCategorySpinner = (Spinner)findViewById(R.id.spinner_categories);
        mCategorySpinner.setOnItemSelectedListener(this);
        mPaymentMethodSpinner = (Spinner)findViewById(R.id.spinner_payment_methods);
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

    private ExpenseWrapper getExpenseWrapper() {
        return ExpenseHelper.getExpenseWrapper(ExpenseViewType.ALL);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == mCategorySpinner) {

        } else if (adapterView == mPaymentMethodSpinner) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        if (mDatePicker1.isSelected()) {
            handleDatePicked(year, month, day, true);
        } else {
            handleDatePicked(year, month, day, false);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mDatePicker1) {
            mDatePicker1.setSelected(true);
            mDatePicker2.setSelected(false);
            showDatePicker(true);
        } else if (view == mDatePicker2) {
            mDatePicker1.setSelected(false);
            mDatePicker2.setSelected(true);
            showDatePicker(false);
        }
    }

    private void showDatePicker(boolean isFirstDate) {
        Bundle args = new Bundle();
        if (isFirstDate) {
            args.putInt(Others.DATE_PICKER_YEAR, mYear1);
            args.putInt(Others.DATE_PICKER_MONTH, mMonth1);
            args.putInt(Others.DATE_PICKER_DAY, mDay1);
        } else {
            args.putInt(Others.DATE_PICKER_YEAR, mYear2);
            args.putInt(Others.DATE_PICKER_MONTH, mMonth2);
            args.putInt(Others.DATE_PICKER_DAY, mDay2);
        }
        DialogFragment newFragment = DatePickerDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),
                DatePickerDialogFragment.TAG);
    }

    private void handleDatePicked(int year, int month, int day, boolean isFirstDate) {
        if (isFirstDate) {
            mYear1 = year;
            mMonth1 = month;
            mDay1 = day;
            mDatePicker1.setText(DateHelper.getPrettyDate(year, month, day));
        } else {
            mYear2 = year;
            mMonth2 = month;
            mDay2 = day;
            mDatePicker2.setText(DateHelper.getPrettyDate(year, month, day));
        }
    }
}

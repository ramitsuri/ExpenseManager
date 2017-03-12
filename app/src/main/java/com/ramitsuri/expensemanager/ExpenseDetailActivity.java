package com.ramitsuri.expensemanager;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.DateHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.dialog.CategoryPickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.CurrencyPickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.DatePickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.PaymentPickerDialogFragment;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;

import java.math.BigDecimal;

public class ExpenseDetailActivity extends AppCompatActivity implements View.OnClickListener,
        PaymentPickerDialogFragment.PaymentMethodPickerCallbacks,
        DatePickerDialogFragment.DatePickerCallbacks,
        CategoryPickerDialogFragment.CategoryPickerCallbacks,
        CurrencyPickerDialogFragment.CurrencyPickerCallbacks{

    EditText mFieldAmount, mFieldDescription, mFieldStore;
    RelativeLayout mDatePicker, mCategoryPicker, mPaymentMethodPicker;
    TextView mDatePickerText, mCategoryPickerText, mPaymentMethodPickerText;
    Button mCurrencyPicker;
    private FloatingActionButton mFabDone;
    private Toolbar mToolbar;
    private Expense mExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        mExpense = new Expense();

        setupActionBar();
        setupView();
        setupListeners();
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupView() {
        mDatePicker = (RelativeLayout) findViewById(R.id.date_picker);
        mCategoryPicker = (RelativeLayout)findViewById(R.id.category_picker);
        mPaymentMethodPicker = (RelativeLayout)findViewById(R.id.payment_method_picker);
        mCurrencyPicker = (Button)findViewById(R.id.currency_picker);
        mFieldAmount = (EditText)findViewById(R.id.edit_text_amount);
        mFieldDescription = (EditText)findViewById(R.id.edit_text_description);
        mFieldStore = (EditText)findViewById(R.id.edit_text_store);
        mFabDone = (FloatingActionButton)findViewById(R.id.fab_done);
        mDatePickerText = (TextView)findViewById(R.id.date_picker_text);
        mCategoryPickerText = (TextView)findViewById(R.id.category_picker_text);
        mPaymentMethodPickerText = (TextView)findViewById(R.id.payment_method_picker_text);

        long date = DateHelper.getTodaysLongDate();
        //handleCategoryPicked();
        handleDatePicked(DateHelper.getYearFromLongDate(date),
                DateHelper.getMonthFromLongDate(date) - 1, DateHelper.getDayFromLongDate(date));
        //handlePaymentPicked();
    }

    private void setupListeners() {
        mDatePicker.setOnClickListener(this);
        mCategoryPicker.setOnClickListener(this);
        mPaymentMethodPicker.setOnClickListener(this);
        mCurrencyPicker.setOnClickListener(this);
        mFabDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mDatePicker){
            DialogFragment newFragment = new DatePickerDialogFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        } else if(view == mCategoryPicker){
            DialogFragment newFragment = new CategoryPickerDialogFragment();
            newFragment.show(getSupportFragmentManager(), "categoryPicker");
        } else if(view == mPaymentMethodPicker){
            DialogFragment newFragment = new PaymentPickerDialogFragment();
            newFragment.show(getSupportFragmentManager(), "paymentMethodPicker");
        } else if(view == mCurrencyPicker){
            DialogFragment newFragment = new CurrencyPickerDialogFragment();
            newFragment.show(getSupportFragmentManager(), "currencyPicker");
        } else if(view == mFabDone){
            createExpense();
            finish();
        }
    }

    private void createExpense() {
        mExpense.setRowIdentifier("1");
        mExpense.setStore(mFieldStore.getEditableText().toString());
        mExpense.setDescription(mFieldDescription.getEditableText().toString());
        String amount = mFieldAmount.getEditableText().toString();
        if(amount.isEmpty()){
            amount = "0";
        }
        mExpense.setAmount(new BigDecimal(amount));
        ExpenseHelper.addExpense(mExpense);
    }

    @Override
    public void onPaymentMethodPicked(PaymentMethod paymentMethod) {
        handlePaymentPicked(paymentMethod);
    }

    private void handlePaymentPicked(PaymentMethod paymentMethod) {
        mExpense.setPaymentMethod(paymentMethod);
        mPaymentMethodPickerText.setText(paymentMethod.toString());
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        handleDatePicked(year, month, day);
    }

    private void handleDatePicked(int year, int month, int day) {
        mDatePickerText.setText(DateHelper.getPrettyDate(year, month, day));
        mExpense.setDateTime(DateHelper.getLongDateForDB(year, month, day));
    }

    @Override
    public void onCurrencyPicked(String currency) {
    }

    @Override
    public void onCategoryPicked(Category category) {
        handleCategoryPicked(category);
    }

    private void handleCategoryPicked(Category category) {
        mExpense.setCategory(category);
        mCategoryPickerText.setText(category.toString());
    }
}

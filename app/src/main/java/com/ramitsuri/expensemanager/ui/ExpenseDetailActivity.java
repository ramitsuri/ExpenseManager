package com.ramitsuri.expensemanager.ui;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.dialog.CategoryPickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.CurrencyPickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.DatePickerDialogFragment;
import com.ramitsuri.expensemanager.dialog.PaymentMethodPickerDialogFragment;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.DateHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.math.BigDecimal;
import java.util.Calendar;

public class ExpenseDetailActivity extends AppCompatActivity implements View.OnClickListener,
        PaymentMethodPickerDialogFragment.PaymentMethodPickerCallbacks,
        DatePickerDialogFragment.DatePickerCallbacks,
        CategoryPickerDialogFragment.CategoryPickerCallbacks,
        CurrencyPickerDialogFragment.CurrencyPickerCallbacks {

    EditText mFieldAmount, mFieldDescription, mFieldStore;
    RelativeLayout mDatePicker, mCategoryPicker, mPaymentMethodPicker;
    TextView mDatePickerText, mCategoryPickerText, mPaymentMethodPickerText;
    Button mCurrencyPicker;
    private FloatingActionButton mFabDone;
    private Toolbar mToolbar;
    private Expense mExpense;
    private int mYear, mMonth, mDay;
    private Category mCategory;
    private PaymentMethod mPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        mExpense = new Expense();

        setupActionBar();
        setupView();
        setupListeners();
        setupDate();
        mCategory = CategoryHelper.getFirstCategory();
        mPaymentMethod = PaymentMethodHelper.getFirstPaymentMethod();
    }

    private void setupDate() {
        Calendar calendar = Calendar.getInstance();
        handleDatePicked(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupActionBar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupView() {
        mDatePicker = (RelativeLayout)findViewById(R.id.date_picker);
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
        onCurrencyPicked(AppHelper.getCurrency());
        handleCategoryPicked(CategoryHelper.getFirstCategory());
        handlePaymentPicked(PaymentMethodHelper.getFirstPaymentMethod());
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
        if (view == mDatePicker) {
            showDatePicker();
        } else if (view == mCategoryPicker) {
            showCategoryPicker();
        } else if (view == mPaymentMethodPicker) {
            showPaymentMethodPicker();
        } else if (view == mCurrencyPicker) {
            DialogFragment newFragment = CurrencyPickerDialogFragment.newInstance();
            newFragment.show(getSupportFragmentManager(), CurrencyPickerDialogFragment.TAG);
        } else if (view == mFabDone) {
            createExpense();
            finish();
        }
    }

    private void showPaymentMethodPicker() {
        Bundle args = new Bundle();
        args.putParcelable(Others.PAYMENT_METHOD_PICKER_METHOD, mPaymentMethod);
        DialogFragment newFragment = PaymentMethodPickerDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), PaymentMethodPickerDialogFragment.TAG);
    }

    private void showCategoryPicker() {
        Bundle args = new Bundle();
        args.putParcelable(Others.CATEGORY_PICKER_CATEGORY, mCategory);
        DialogFragment newFragment = CategoryPickerDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), CategoryPickerDialogFragment.TAG);
    }

    private void showDatePicker() {
        Bundle args = new Bundle();
        args.putInt(Others.DATE_PICKER_YEAR, mYear);
        args.putInt(Others.DATE_PICKER_MONTH, mMonth);
        args.putInt(Others.DATE_PICKER_DAY, mDay);
        DialogFragment newFragment = DatePickerDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),
                DatePickerDialogFragment.TAG);
    }

    private void createExpense() {
        mExpense.setStore(mFieldStore.getEditableText().toString());
        mExpense.setDescription(mFieldDescription.getEditableText().toString());
        String amount = mFieldAmount.getEditableText().toString();
        if (amount.isEmpty()) {
            amount = "0";
        }
        mExpense.setAmount(new BigDecimal(amount));
        mExpense.setPaymentMethod(mPaymentMethod);
        mExpense.setCategory(mCategory);
        ExpenseHelper.addExpense(mExpense);
    }

    @Override
    public void onPaymentMethodPicked(PaymentMethod paymentMethod) {
        handlePaymentPicked(paymentMethod);
    }

    private void handlePaymentPicked(PaymentMethod paymentMethod) {
        mPaymentMethod = paymentMethod;
        mPaymentMethodPickerText.setText(paymentMethod.toString());
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        handleDatePicked(year, month, day);
    }

    private void handleDatePicked(int year, int month, int day) {
        mDatePickerText.setText(DateHelper.getPrettyDate(year, month, day));
        mExpense.setDateTime(DateHelper.getLongDateForDB(year, month, day));
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    @Override
    public void onCurrencyPicked(String currency) {
        AppHelper.setCurrency(currency);
        mCurrencyPicker.setText(currency.split("-")[1]);
    }

    @Override
    public void onCategoryPicked(Category category) {
        handleCategoryPicked(category);
    }

    private void handleCategoryPicked(Category category) {
        mCategory = category;
        mCategoryPickerText.setText(category.toString());
    }
}

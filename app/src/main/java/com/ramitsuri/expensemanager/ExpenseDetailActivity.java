package com.ramitsuri.expensemanager;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ramitsuri.expensemanager.dialog.DatePickerDialogFragment;

public class ExpenseDetailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView mDatePicker, mCategoryPicker, mPaymentMethodPicker;
    EditText mFieldAmount, mFieldDescription;
    Button mCurrencyPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        setupView();
        setupListeners();
    }

    private void setupView() {
        mDatePicker = (TextView)findViewById(R.id.date_picker);
        mCategoryPicker = (TextView)findViewById(R.id.category_picker);
        mPaymentMethodPicker = (TextView)findViewById(R.id.payment_method_picker);
        mCurrencyPicker = (Button)findViewById(R.id.currency_picker);
        mFieldAmount = (EditText)findViewById(R.id.edit_text_amount);
        mFieldDescription = (EditText)findViewById(R.id.edit_text_description);
    }

    private void setupListeners() {
        mDatePicker.setOnClickListener(this);
        mCategoryPicker.setOnClickListener(this);
        mPaymentMethodPicker.setOnClickListener(this);
        mCurrencyPicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mDatePicker){
            DialogFragment newFragment = new DatePickerDialogFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        } else if(view == mCategoryPicker){

        } else if(view == mPaymentMethodPicker){

        } else if(view == mCurrencyPicker){

        }
    }
}

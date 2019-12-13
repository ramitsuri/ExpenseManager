package com.ramitsuri.expensemanager.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanager.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.AddExpenseViewModel;
import com.ramitsuri.expensemanager.viewModel.ViewModelFactory;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import timber.log.Timber;

public class AddExpenseFragment extends BaseFragment implements View.OnClickListener,
        DatePickerDialog.DatePickerDialogCallback {

    private static final String EMPTY = "<empty>";

    // Data
    private AddExpenseViewModel mViewModel;

    // Views
    private ImageView mBtnClose;
    private EditText mEditStore, mEditAmount, mEditDescription;
    private ViewGroup mLayoutDate;
    private TextView mTextDate;
    private ExtendedFloatingActionButton mBtnDone;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        showActionBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Expense expense = null;
        if (getArguments() != null) {
            expense = AddExpenseFragmentArgs.fromBundle(getArguments()).getExpense();
        }
        mViewModel = ViewModelProviders.of(this, new ViewModelFactory(expense))
                .get(AddExpenseViewModel.class);

        setupViews(view, expense);

        setupRecyclerViews(view, expense);
    }

    private void setupViews(View view, @Nullable Expense expense) {
        // Close
        mBtnClose = view.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);

        // EditTexts
        mEditStore = view.findViewById(R.id.edit_text_store);
        mEditAmount = view.findViewById(R.id.edit_text_amount);
        mEditDescription = view.findViewById(R.id.edit_text_description);

        // ViewGroups
        mLayoutDate = view.findViewById(R.id.layout_date);
        mLayoutDate.setOnClickListener(this);

        // TextViews
        mTextDate = view.findViewById(R.id.text_date);

        // Done
        mBtnDone = view.findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);

        if (expense != null) {
            // Store
            String value = expense.getStore();
            if (!TextUtils.isEmpty(value) && !value.equals(EMPTY)) {
                mEditStore.setText(value);
                mEditStore.setSelection(value.length());
            }
            // Description
            value = expense.getDescription();
            if (!TextUtils.isEmpty(value) && !value.equals(EMPTY)) {
                mEditDescription.setText(value);
                mEditDescription.setSelection(value.length());
            }
            // Date
            long longValue = expense.getDateTime();
            mTextDate.setText(DateHelper.getFriendlyDate(longValue));

            // Amount
            value = CurrencyHelper.formatForDisplay(true, expense.getAmount());
            if (value != null) {
                mEditAmount.setText(value);
                mEditAmount.setSelection(value.length());
            }
        }
    }

    private void setupRecyclerViews(View view, @Nullable final Expense expense) {
        // Categories
        RecyclerView listCategories = view.findViewById(R.id.list_categories);
        listCategories.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL));
        listCategories.setHasFixedSize(true);
        final ListPickerAdapter categoriesAdapter = new ListPickerAdapter();
        categoriesAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(String value) {
                onCategoryPicked(value);
            }
        });
        listCategories.setAdapter(categoriesAdapter);
        mViewModel.getCategories().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                Timber.i("Categories received %s", categories);
                String selectedValue = null;
                if (expense != null) {
                    selectedValue = expense.getCategory();
                }
                categoriesAdapter.setValues(categories, selectedValue);
            }
        });

        // Payment Methods
        RecyclerView listPaymentMethods = view.findViewById(R.id.list_payment_methods);
        listPaymentMethods.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL));
        listPaymentMethods.setHasFixedSize(true);
        final ListPickerAdapter paymentMethodsAdapter = new ListPickerAdapter();
        paymentMethodsAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(String value) {
                onPaymentMethodPicked(value);
            }
        });
        listPaymentMethods.setAdapter(paymentMethodsAdapter);
        mViewModel.getPaymentMethods().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> paymentMethods) {
                Timber.i("Payment Methods received %s", paymentMethods);
                String selectedValue = null;
                if (expense != null) {
                    selectedValue = expense.getPaymentMethod();
                }
                paymentMethodsAdapter.setValues(paymentMethods, selectedValue);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutDate) {
            handleDateClicked();
        } else if (view == mBtnClose) {
            handleCloseFragmentClicked();
        } else if (view == mBtnDone) {
            handleDoneClicked();
        }
    }

    private void handleCloseFragmentClicked() {
        removeFocusAndHideKeyboard();
        if (mViewModel.isChangesMade()) {
            if (getContext() != null) {
                DialogInterface.OnClickListener negativeListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exitToUp();
                            }
                        };
                DialogHelper.showAlert(getContext(),
                        R.string.common_warning, R.string.exit_while_editing_warning_message,
                        R.string.common_keep_editing, null,
                        R.string.common_discard, negativeListener);
                return;
            }
        }
        exitToUp();
    }

    private void handleDateClicked() {
        removeFocusAndHideKeyboard();
        LocalDate localDate = DateHelper.getLocalDate(new Date(mViewModel.getExpenseDate()));
        int year = DateHelper.getYearFromDate(localDate);
        int month = DateHelper.getMonthFromDate(localDate);
        int day = DateHelper.getDayFromDate(localDate);

        DatePickerDialog dialog = DatePickerDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_YEAR, year);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_MONTH, month);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_DAY, day);
        dialog.setArguments(bundle);
        dialog.setCallback(this);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), DatePickerDialog.TAG);
        } else {
            Timber.e(
                    "handleDateClicked -> getActivity() returned null when showing date picker dialog");
        }
    }

    private void handleDoneClicked() {
        Timber.i("Attempting save");
        removeFocusAndHideKeyboard();
        mViewModel.setExpenseAmount(getExpenseAmount());
        mViewModel.setExpenseStore(getExpenseStore());
        mViewModel.setExpenseDescription(getExpenseDescription());
        if (mViewModel.getAddMode() == Constants.AddExpenseMode.ADD) {
            mViewModel.addExpense();
        } else {
            mViewModel.editExpense();
        }
        exitToUp();
    }

    private void onCategoryPicked(String value) {
        Timber.i("Category picked: %s", value);
        mViewModel.setExpenseCategory(value);
        removeFocusAndHideKeyboard();
    }

    private void onPaymentMethodPicked(String value) {
        Timber.i("Payment method picked: %s", value);
        mViewModel.setExpensePaymentMethod(value);
        removeFocusAndHideKeyboard();
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        Timber.i("Date picked: %s/%s/%s", month + 1, day, year);
        long pickedDate = DateHelper.getDateFromYearMonthDay(year, month, day);
        mTextDate.setText(DateHelper.getFriendlyDate(pickedDate));
        mViewModel.setExpenseDate(pickedDate);
    }

    private String getExpenseAmount() {
        if (mEditAmount != null) {
            String text = mEditAmount.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return "0.00";
    }

    private String getExpenseStore() {
        if (mEditStore != null) {
            String text = mEditStore.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return EMPTY;
    }

    private String getExpenseDescription() {
        if (mEditDescription != null) {
            String text = mEditDescription.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return EMPTY;
    }

    private void removeFocusAndHideKeyboard() {
        // Remove from Amount field
        if (mEditAmount.hasFocus()) {
            mEditAmount.clearFocus();
            if (getActivity() != null) { // close keyboard
                hideKeyboardFrom(getActivity(), mEditAmount);
            }
        }
        // Remove from Store field
        if (mEditStore.hasFocus()) {
            mEditStore.clearFocus();
            if (getActivity() != null) { // close keyboard
                hideKeyboardFrom(getActivity(), mEditStore);
            }
        }
        // Remove from Description field
        if (mEditDescription.hasFocus()) {
            mEditDescription.clearFocus();
            if (getActivity() != null) { // close keyboard
                hideKeyboardFrom(getActivity(), mEditDescription);
            }
        }
    }
}

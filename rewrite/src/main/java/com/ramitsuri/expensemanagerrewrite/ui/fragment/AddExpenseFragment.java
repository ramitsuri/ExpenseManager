package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ramitsuri.expensemanagerrewrite.Constants;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanagerrewrite.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanagerrewrite.utils.DateHelper;
import com.ramitsuri.expensemanagerrewrite.utils.DialogHelper;
import com.ramitsuri.expensemanagerrewrite.viewModel.AddExpenseViewModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
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
    private FloatingActionButton mBtnDone;

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

        mViewModel = ViewModelProviders.of(this).get(AddExpenseViewModel.class);

        setupViews(view);

        setupRecyclerViews(view);
    }

    private void setupViews(View view) {
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
    }

    private void setupRecyclerViews(View view) {
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
                categoriesAdapter.setValues(categories);
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
                paymentMethodsAdapter.setValues(paymentMethods);
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
                        R.string.warning, R.string.exit_while_editing_warning_message,
                        R.string.keep_editing, null,
                        R.string.discard, negativeListener);
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
        mViewModel.addExpense();
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

    private void exitToUp() {
        Activity activity = getActivity();
        if (activity != null) {
            ((AppCompatActivity)activity).onSupportNavigateUp();
        } else {
            Timber.w("handleCloseFragmentClicked() -> Activity is null");
        }
    }

    private String getExpenseAmount() {
        if (mEditAmount != null) {
            return mEditAmount.getText().toString().trim();
        }
        return EMPTY;
    }

    private String getExpenseStore() {
        if (mEditStore != null) {
            return mEditStore.getText().toString().trim();
        }
        return EMPTY;
    }

    private String getExpenseDescription() {
        if (mEditDescription != null) {
            return mEditDescription.getText().toString().trim();
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

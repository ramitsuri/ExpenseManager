package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.utils.DateHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ExpenseDetailsFragment extends BottomSheetDialogFragment {

    static final String TAG = ExpenseDetailsFragment.class.getName();

    static ExpenseDetailsFragment newInstance() {
        return new ExpenseDetailsFragment();
    }

    private DetailFragmentCallback mCallback;

    public interface DetailFragmentCallback {
        void onEditRequested(@NonNull Expense expense);
    }

    public void setCallback(@NonNull DetailFragmentCallback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Expense expense = getArguments().getParcelable(Constants.BundleKeys.SELECTED_EXPENSE);
            if (expense != null) {
                setupViews(view, expense);
            }
        }
    }

    private void setupViews(@NonNull View view, @NonNull final Expense expense) {
        // Description
        TextView txtDescription = view.findViewById(R.id.txt_expense_description);
        txtDescription.setText(expense.getDescription());

        // Amount
        TextView txtAmount = view.findViewById(R.id.txt_expense_amount);
        txtAmount.setText(txtAmount.getContext()
                .getString(R.string.amount_with_currency, String.valueOf(expense.getAmount())));

        // Date
        TextView txtDate = view.findViewById(R.id.text_expense_date);
        txtDate.setText(DateHelper.getExpandedDate(expense.getDateTime()));

        // Store
        TextView txtDetail1 = view.findViewById(R.id.text_expense_detail_1);
        if (TextUtils.isEmpty(expense.getStore())) {
            txtDetail1.setVisibility(View.GONE);
        } else {
            txtDetail1.setText(expense.getStore());
        }

        // Category
        TextView txtDetail2 = view.findViewById(R.id.text_expense_detail_2);
        txtDetail2.setText(expense.getCategory());

        // Payment method
        TextView txtDetail3 = view.findViewById(R.id.txt_expense_detail_3);
        txtDetail3.setText(expense.getPaymentMethod());

        // Edit button
        Button editButton = view.findViewById(R.id.btn_edit);
        if (expense.isSynced()) {
            editButton.setEnabled(false);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onEditRequested(expense);
                }
            }
        });
    }
}

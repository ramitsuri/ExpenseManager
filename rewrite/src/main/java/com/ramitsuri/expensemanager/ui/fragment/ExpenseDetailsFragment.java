package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.AddType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ExpenseDetailsFragment extends BaseBottomSheetFragment {

    static final String TAG = ExpenseDetailsFragment.class.getName();

    static ExpenseDetailsFragment newInstance() {
        return new ExpenseDetailsFragment();
    }

    private DetailFragmentCallback mCallback;

    public interface DetailFragmentCallback {
        void onEditRequested(@NonNull Expense expense);

        void onDeleteRequested(@NonNull Expense expense);

        void onDuplicateRequested(@Nonnull Expense expense);

        void onPushToRemoteSharedRequested(@Nonnull Expense expense);
    }

    public void setCallback(@NonNull DetailFragmentCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_details, container, false);
        setSystemUiVisibility(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Expense expense = getArguments().getParcelable(Constants.BundleKeys.SELECTED_EXPENSE);
            boolean enableShared = getArguments().getBoolean(Constants.BundleKeys.ENABLE_SHARED);
            if (expense != null) {
                setupViews(view, expense, enableShared);
            }
        }
    }

    private void setupViews(@NonNull View view, @NonNull final Expense expense,
            boolean enableShared) {
        // Description
        TextView txtDescription = view.findViewById(R.id.txt_expense_description);
        txtDescription.setText(expense.getDescription());

        // Amount
        TextView txtAmount = view.findViewById(R.id.txt_expense_amount);
        txtAmount.setText(CurrencyHelper.formatForDisplay(true, expense.getAmount()));

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

        // Star/Flag
        TextView txtFlagStatus = view.findViewById(R.id.text_flag_status);
        if (expense.isStarred()) {
            txtFlagStatus.setVisibility(View.VISIBLE);
        }

        // Add Status
        TextView txtAddStatus = view.findViewById(R.id.text_add_status);
        if (AddType.RECUR.equals(expense.getAddType())) {
            txtAddStatus.setVisibility(View.VISIBLE);
        }

        // Edit button
        Button editButton = view.findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onEditRequested(expense);
                }
            }
        });

        // Duplicate button
        Button duplicateButton = view.findViewById(R.id.btn_duplicate);
        duplicateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onDuplicateRequested(expense);
                }
            }
        });

        // Delete button
        Button deleteButton = view.findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onDeleteRequested(expense);
                }
            }
        });

        // Shared push to remote button
        Button pushToRemoteButton = view.findViewById(R.id.btn_add_shared);
        if (enableShared) {
            pushToRemoteButton.setVisibility(View.VISIBLE);
            pushToRemoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (mCallback != null) {
                        mCallback.onPushToRemoteSharedRequested(expense);
                    }
                }
            });
        }
    }
}

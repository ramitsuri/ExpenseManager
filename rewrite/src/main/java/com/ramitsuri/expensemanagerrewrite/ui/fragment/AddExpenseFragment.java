package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramitsuri.expensemanagerrewrite.Constants;
import com.ramitsuri.expensemanagerrewrite.IntDefs.PickedItem;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.data.DummyData;
import com.ramitsuri.expensemanagerrewrite.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanagerrewrite.ui.dialog.ListPickerDialog;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

public class AddExpenseFragment extends Fragment implements View.OnClickListener,
        ListPickerDialog.ListPickerDialogCallback,
        DatePickerDialog.DatePickerDialogCallback {

    private EditText mEditStore, mEditAmount, mEditDescription;
    private TextView mTxtDate, mTxtCategory, mTxtPaymentMethod;
    private ImageView mImgDate, mImgCategory, mImgPaymentMethod;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // EditTexts
        mEditStore = view.findViewById(R.id.edit_text_store);
        mEditAmount = view.findViewById(R.id.edit_text_amount);
        mEditDescription = view.findViewById(R.id.edit_text_description);

        // TextViews
        mTxtDate = view.findViewById(R.id.text_date);
        mTxtDate.setOnClickListener(this);
        mTxtCategory = view.findViewById(R.id.text_category);
        mTxtCategory.setOnClickListener(this);
        mTxtPaymentMethod = view.findViewById(R.id.text_payment_method);
        mTxtPaymentMethod.setOnClickListener(this);

        // ImageViews
        mImgDate = view.findViewById(R.id.image_date);
        mImgDate.setOnClickListener(this);
        mImgCategory = view.findViewById(R.id.image_category);
        mImgCategory.setOnClickListener(this);
        mImgPaymentMethod = view.findViewById(R.id.image_payment_method);
        mImgPaymentMethod.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mTxtDate || view == mImgDate) {
            handleDateClicked();
        } else if (view == mTxtCategory || view == mImgCategory) {
            handleCategoryClicked();
        } else if (view == mTxtPaymentMethod || view == mImgPaymentMethod) {
            handlePaymentMethodClicked();
        }
    }

    private void handleDateClicked() {
        DatePickerDialog dialog = DatePickerDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_YEAR, 2019);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_MONTH, 7);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_DAY, 22);
        dialog.setArguments(bundle);
        dialog.setCallback(this);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), DatePickerDialog.TAG);
        } else {
            Timber.e("getActivity() returned null when showing date picker dialog");
        }
    }

    private void handleCategoryClicked() {
        ArrayList<String> values = new ArrayList<>(Arrays.asList(DummyData.getCategories()));
        showListPickerDialog(PickedItem.CATEGORY, values);
    }

    private void handlePaymentMethodClicked() {
        ArrayList<String> values = new ArrayList<>(Arrays.asList(DummyData.getPaymentMethods()));
        showListPickerDialog(PickedItem.PAYMENT_METHOD, values);
    }

    @Override
    public void onItemPicked(@PickedItem int pickedItem, String value) {
        if (pickedItem == PickedItem.CATEGORY) {
            onCategoryPicked(value);
        } else if (pickedItem == PickedItem.PAYMENT_METHOD) {
            onPaymentMethodPicked(value);
        }
    }

    private void onCategoryPicked(String value) {
        Timber.i("Category picked: %s", value);
    }

    private void onPaymentMethodPicked(String value) {
        Timber.i("Payment method picked: %s", value);
    }

    private void showListPickerDialog(@PickedItem int pickedItem, ArrayList<String> values) {
        ListPickerDialog dialog = ListPickerDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.PICKED_ITEM, pickedItem);
        bundle.putStringArrayList(Constants.BundleKeys.PICKER_VALUES, values);
        dialog.setArguments(bundle);
        dialog.setCallback(this);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), ListPickerDialog.TAG);
        } else {
            Timber.e("getActivity() returned null when showing picker dialog");
        }
    }

    @Override
    public void onDatePicked(int year, int month, int day) {

    }
}

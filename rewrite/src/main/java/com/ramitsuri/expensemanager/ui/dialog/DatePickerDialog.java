package com.ramitsuri.expensemanager.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerDialog extends DialogFragment {

    public static final String TAG = DatePickerDialog.class.getName();

    private DatePickerDialogCallback mCallback;

    public void setCallback(@NonNull DatePickerDialogCallback callback) {
        mCallback = callback;
    }

    public interface DatePickerDialogCallback {
        void onDatePicked(int year, int month, int day);
    }

    public static DatePickerDialog newInstance() {
        return new DatePickerDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_date_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            int year = getArguments().getInt(Constants.BundleKeys.DATE_PICKER_YEAR);
            int month = getArguments().getInt(Constants.BundleKeys.DATE_PICKER_MONTH);
            int day = getArguments().getInt(Constants.BundleKeys.DATE_PICKER_DAY);

            DatePicker datePicker = view.findViewById(R.id.date_picker);
            datePicker.init(year, month, day, mListener);
        }
    }

    private DatePicker.OnDateChangedListener mListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            mCallback.onDatePicked(year, month, day);
            dismiss();
        }
    };
}

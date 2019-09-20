package com.ramitsuri.expensemanagerlegacy.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.constants.Others;

public class DatePickerDialogFragment extends DialogFragment {

    public static String TAG = DatePickerDialogFragment.class.getName();
    private DatePickerCallbacks mCallbacks;
    private int mYear;
    private int mMonth;
    private int mDay;

    public interface DatePickerCallbacks {
        void onDatePicked(int year, int month, int day);
    }

    public static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt(Others.DATE_PICKER_YEAR);
        mMonth = getArguments().getInt(Others.DATE_PICKER_MONTH);
        mDay = getArguments().getInt(Others.DATE_PICKER_DAY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_date_picker, container, false);
        DatePicker datePicker = (DatePicker)v.findViewById(R.id.date_picker);
        datePicker.init(mYear, mMonth, mDay, mListener);
        return v;
    }

    DatePicker.OnDateChangedListener mListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            mCallbacks.onDatePicked(year, month, day);
            dismiss();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (DatePickerCallbacks)context;
    }
}

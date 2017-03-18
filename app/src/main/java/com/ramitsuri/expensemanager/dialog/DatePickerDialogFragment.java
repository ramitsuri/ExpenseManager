package com.ramitsuri.expensemanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;


public class DatePickerDialogFragment extends DialogFragment {

    public static String TAG = DatePickerDialogFragment.class.getName();
    private DatePickerCallbacks mCallbacks;
    private DatePicker mDatePicker;
    private int mYear;
    private int mMonth;
    private int mDay;

    public interface DatePickerCallbacks{
        void onDatePicked(int year, int month, int day);
    }

    public static DatePickerDialogFragment newInstance(){
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
        mDatePicker = (DatePicker) v.findViewById(R.id.date_picker);
        mDatePicker.init(mYear, mMonth, mDay, mListener);
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
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (DatePickerCallbacks) context;
    }
}

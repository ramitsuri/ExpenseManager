package com.ramitsuri.expensemanager.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    private DatePickerCallbacks mCallbacks;
    public interface DatePickerCallbacks{
        void onDatePicked(int year, int month, int day);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (DatePickerCallbacks) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mCallbacks.onDatePicked(year, month, day);
    }
}

package com.ramitsuri.expensemanagerlegacy.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.ramitsuri.expensemanagerlegacy.R;

import java.util.ArrayList;
import java.util.List;

public class CurrencyPickerDialogFragment extends DialogFragment {

    public static String TAG = CurrencyPickerDialogFragment.class.getName();

    public static CurrencyPickerDialogFragment newInstance() {
        return new CurrencyPickerDialogFragment();
    }

    private CurrencyPickerCallbacks mCallbacks;

    public interface CurrencyPickerCallbacks {
        void onCurrencyPicked(String currency);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (CurrencyPickerCallbacks)context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> values = getCurrencies();
        final CharSequence[] items = values.toArray(new CharSequence[values.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.currency_picker_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onCurrencyPicked(String.valueOf(items[which]));
                    }
                });
        return builder.create();
    }

    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setLayout(840, 1360);
    }

    public List<String> getCurrencies() {
        List currencies = new ArrayList<>();
        currencies.add("INR - ₹");
        currencies.add("USD - $");
        currencies.add("CAD - $");
        return currencies;
    }
}

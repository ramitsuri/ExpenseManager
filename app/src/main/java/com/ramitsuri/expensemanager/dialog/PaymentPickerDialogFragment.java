package com.ramitsuri.expensemanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.ramitsuri.expensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentPickerDialogFragment extends DialogFragment {

    private PaymentMethodPickerCallbacks mCallbacks;
    public interface PaymentMethodPickerCallbacks{
        void onPaymentMethodPicked(String paymentMethod);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (PaymentMethodPickerCallbacks)context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> values = getPaymentMethods();
        final CharSequence[] items = values.toArray(new CharSequence[values.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.payment_method_picker_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onPaymentMethodPicked(String.valueOf(items[which]));
                    }
                });
        return builder.create();
    }

    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setLayout(840, 1360);
    }

    public List<String> getPaymentMethods(){
        List mPaymentMethods = new ArrayList<>();
        mPaymentMethods.add("Discover");
        mPaymentMethods.add("Cash");
        mPaymentMethods.add("WF Checking");
        mPaymentMethods.add("WF Savings");
        mPaymentMethods.add("Amazon");
        return mPaymentMethods;
    }
}

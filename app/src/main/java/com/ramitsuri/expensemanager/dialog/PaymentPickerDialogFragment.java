package com.ramitsuri.expensemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PaymentPickerDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> values = getPaymentMethods();
        final CharSequence[] items = values.toArray(new CharSequence[values.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Payment Method")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), items[which], Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }

    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(840, 1360);
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

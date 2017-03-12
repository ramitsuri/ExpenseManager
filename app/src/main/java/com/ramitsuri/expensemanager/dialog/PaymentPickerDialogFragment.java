package com.ramitsuri.expensemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.util.ArrayList;
import java.util.List;

public class PaymentPickerDialogFragment extends DialogFragment {

    private List<PaymentMethod> mPaymentMethods;
    private List<String> mPaymentMethodNames;
    private PaymentMethodPickerCallbacks mCallbacks;
    public interface PaymentMethodPickerCallbacks{
        void onPaymentMethodPicked(PaymentMethod paymentMethod);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (PaymentMethodPickerCallbacks)context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buildPaymentMethods();
        final CharSequence[] items =
                mPaymentMethodNames.toArray(new CharSequence[mPaymentMethodNames.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.payment_method_picker_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onPaymentMethodPicked(mPaymentMethods.get(which));
                    }
                });
        return builder.create();
    }

    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setLayout(840, 1360);
    }

    public void buildPaymentMethods(){
        mPaymentMethods = PaymentMethodHelper.getAllPaymentMethods();
        mPaymentMethodNames = new ArrayList<>();
        for(PaymentMethod paymentMethod: mPaymentMethods){
            mPaymentMethodNames.add(paymentMethod.toString());
        }
        /*mPaymentMethods.add("Discover");
        mPaymentMethods.add("Cash");
        mPaymentMethods.add("WF Checking");
        mPaymentMethods.add("WF Savings");
        mPaymentMethods.add("Amazon");
        return mPaymentMethods;*/
    }
}

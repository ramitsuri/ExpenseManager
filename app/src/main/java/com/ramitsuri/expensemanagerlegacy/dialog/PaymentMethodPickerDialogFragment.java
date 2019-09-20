package com.ramitsuri.expensemanagerlegacy.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanagerlegacy.constants.Others;
import com.ramitsuri.expensemanagerlegacy.entities.PaymentMethod;
import com.ramitsuri.expensemanagerlegacy.helper.PaymentMethodHelper;

public class PaymentMethodPickerDialogFragment extends DialogFragment
        implements ListPickerAdapter.ListPickerAdapterCallbacks {

    public static String TAG = PaymentMethodPickerDialogFragment.class.getName();

    private PaymentMethodPickerCallbacks mCallbacks;

    public interface PaymentMethodPickerCallbacks {
        void onPaymentMethodPicked(PaymentMethod paymentMethod);
    }

    public static PaymentMethodPickerDialogFragment newInstance() {
        return new PaymentMethodPickerDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_list_picker, container, false);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText(getString(R.string.payment_method_picker_title));
        ListPickerAdapter<PaymentMethod> adapter =
                new ListPickerAdapter<>(this, PaymentMethodHelper.getAllPaymentMethods(),
                        (PaymentMethod)getArguments()
                                .getParcelable(Others.PAYMENT_METHOD_PICKER_METHOD));
        RecyclerView paymentMethodsRecyclerView = (RecyclerView)v.findViewById(R.id.values);
        paymentMethodsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getContext());
        paymentMethodsRecyclerView.setLayoutManager(recyclerViewLManager);
        paymentMethodsRecyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (PaymentMethodPickerCallbacks)context;
    }

    @Override
    public void onItemSelected(Object item) {
        dismiss();
        mCallbacks.onPaymentMethodPicked((PaymentMethod)item);
    }
}

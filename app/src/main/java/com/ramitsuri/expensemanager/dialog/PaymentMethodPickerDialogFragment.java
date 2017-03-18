package com.ramitsuri.expensemanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;


public class PaymentMethodPickerDialogFragment extends DialogFragment
        implements ListPickerAdapter.ListPickerAdapterCallbacks{

    public static String TAG = PaymentMethodPickerDialogFragment.class.getName();

    private Context mContext;
    private ListPickerAdapter<PaymentMethod> mAdapter;
    private PaymentMethodPickerCallbacks mCallbacks;
    private TextView mTitle;
    private RecyclerView mPaymentMethodsRecyclerView;

    public interface PaymentMethodPickerCallbacks{
        void onPaymentMethodPicked(PaymentMethod paymentMethod);
    }

    public static PaymentMethodPickerDialogFragment newInstance(){
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
        mTitle = (TextView) v.findViewById(R.id.title);
        mTitle.setText(getString(R.string.payment_method_picker_title));
        mAdapter = new ListPickerAdapter<>(this, PaymentMethodHelper.getAllPaymentMethods(),
                (PaymentMethod) getArguments().getParcelable(Others.PAYMENT_METHOD_PICKER_METHOD));
        mPaymentMethodsRecyclerView = (RecyclerView) v.findViewById(R.id.values);
        mPaymentMethodsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getContext());
        mPaymentMethodsRecyclerView.setLayoutManager(recyclerViewLManager);
        mPaymentMethodsRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (PaymentMethodPickerCallbacks)context;
        mContext = context;
    }

    @Override
    public void onItemSelected(Object item) {
        dismiss();
        mCallbacks.onPaymentMethodPicked((PaymentMethod) item);
    }
}

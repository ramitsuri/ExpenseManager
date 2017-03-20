package com.ramitsuri.expensemanager.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ramitsuri.expensemanager.CategoriesActivity;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;

public class EditTextDialogFragment extends DialogFragment implements View.OnClickListener{

    public static String TAG = EditTextDialogFragment.class.getName();

    private EditTextDialogCallbacks mCallbacks;
    private TextView mTitle, mSave;
    private EditText mValue;
    private String mValueToEdit;

    @Override
    public void onClick(View view) {
        dismiss();
        mCallbacks.onItemEdited(mValue.getEditableText().toString());
    }

    public interface EditTextDialogCallbacks {
        void onItemEdited(String value);
    }

    public static EditTextDialogFragment newInstance(){
        return new EditTextDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValueToEdit = getArguments().getString(Others.CATEGORY_PICKER_CATEGORY);
        if(mValueToEdit == null){
            mValueToEdit = getArguments().getString(Others.PAYMENT_METHOD_PICKER_METHOD);
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (EditTextDialogCallbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_fragment_edit_text, container, false);
        mTitle = (TextView) v.findViewById(R.id.title);
        mValue = (EditText) v.findViewById(R.id.edit_text_value);
        mValue.setText(mValueToEdit);
        if(mValueToEdit.isEmpty()){
            mTitle.setText(getString(R.string.add_new));
        } else {
            mTitle.setText(getString(R.string.edit));
        }
        mSave = (TextView) v.findViewById(R.id.button_save);
        mSave.setOnClickListener(this);
        return v;
    }
}

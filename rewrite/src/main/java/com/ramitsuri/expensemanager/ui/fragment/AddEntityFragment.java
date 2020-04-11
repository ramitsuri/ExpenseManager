package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddEntityFragment extends BaseBottomSheetFragment {

    static final String TAG = AddEntityFragment.class.getName();

    static AddEntityFragment newInstance() {
        return new AddEntityFragment();
    }

    private AddEntityCallback mCallback;

    public interface AddEntityCallback {
        void onChanged(@Nullable String newValue);
    }

    public void setCallback(@NonNull AddEntityCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_entity, container, false);
        setSystemUiVisibility(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String value = getArguments().getString(Constants.BundleKeys.SELECTED_ENTITY);
            setupViews(view, value);
        }
    }

    private void setupViews(@NonNull View view, @Nullable final String value) {
        // Name
        final EditText editName = view.findViewById(R.id.edit_text_name);
        if (value != null) {
            editName.setText(value);
        }
        editName.setSelection(editName.getText().toString().length());

        // Edit button
        final Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                String newValue = editName.getText().toString().trim();
                mCallback.onChanged(newValue);
            }
        });
    }
}

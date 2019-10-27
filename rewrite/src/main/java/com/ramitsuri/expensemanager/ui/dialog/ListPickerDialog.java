package com.ramitsuri.expensemanager.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.IntDefs.PickedItem;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListPickerDialog extends BottomSheetDialogFragment implements
        ListPickerAdapter.ListPickerAdapterCallback {

    public static final String TAG = ListPickerDialog.class.getName();

    @PickedItem
    private int mPickedItem;

    private ListPickerDialogCallback mCallback;

    public void setCallback(@NonNull ListPickerDialogCallback callback) {
        mCallback = callback;
    }

    public interface ListPickerDialogCallback {
        void onItemPicked(@PickedItem int pickedItem, String value);
    }

    public static ListPickerDialog newInstance() {
        return new ListPickerDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_list_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView listValues = view.findViewById(R.id.list_values);
        listValues.setLayoutManager(new LinearLayoutManager(getActivity()));
        listValues.setHasFixedSize(true);

        if (getArguments() != null) {
            List<String> values =
                    getArguments().getStringArrayList(Constants.BundleKeys.PICKER_VALUES);
            /*if (values != null) {
                ListPickerAdapter adapter = new ListPickerAdapter(values, this);
                listValues.setAdapter(adapter);
            }*/

            mPickedItem = getArguments().getInt(Constants.BundleKeys.PICKED_ITEM);
        }
    }

    @Override
    public void onItemPicked(String value) {
        Timber.i("Item picked: %s", value);
        if (mCallback != null) {
            mCallback.onItemPicked(mPickedItem, value);
        }
        dismiss();
    }
}

package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.SheetInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SheetPickerAdapter extends RecyclerView.Adapter<SheetPickerAdapter.ViewHolder> {

    @Nullable
    private List<SheetInfo> mValues;
    @Nullable
    private SheetPickerAdapterCallback mCallback;
    private int mSelectedId;

    public interface SheetPickerAdapterCallback {
        void onItemPicked(SheetInfo value);
    }

    public SheetPickerAdapter() {
    }

    public void setValues(@NonNull List<SheetInfo> values, int selectedId) {
        mValues = values;
        if (selectedId == Constants.UNDEFINED &&
                values.size() > 0) { // Select first value in case selection is null
            SheetInfo firstItem = values.get(0);
            mSelectedId = firstItem.getSheetId();
            // Send callback with selected value when selected value was not found (new expense)
            Timber.i("Selecting first value from list %s", firstItem.toString());
            onSelectionMade(firstItem);
        } else {
            mSelectedId = selectedId;
            notifyDataSetChanged();
        }
    }

    public void setCallback(@NonNull SheetPickerAdapterCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mValues != null) {
            holder.bind(mValues.get(position));
        } else {
            Timber.w("mValues is null");
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            Timber.w("mValues is null");
            return 0;
        }
    }

    private void onSelectionMade(SheetInfo selectedValue) {
        if (mCallback != null) {
            mSelectedId = selectedValue.getSheetId();
            mCallback.onItemPicked(selectedValue);
            notifyDataSetChanged();
        } else {
            Timber.w("mCallback is null");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Chip txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtValue = itemView.findViewById(R.id.value);
            txtValue.setOnClickListener(this);
        }

        private void bind(final SheetInfo value) {
            txtValue.setText(value.getSheetName());
            if (value.getSheetId() == mSelectedId) {
                txtValue.setChecked(true);
            } else {
                txtValue.setChecked(false);
            }
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                if (mValues != null) {
                    onSelectionMade(mValues.get(getAdapterPosition()));
                } else {
                    Timber.w("mValues is null");
                }
            } else {
                Timber.w("getAdapterPosition returned -1");
            }
        }
    }
}

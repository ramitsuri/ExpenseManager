package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListPickerAdapter extends RecyclerView.Adapter<ListPickerAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    @Nullable
    private ListPickerAdapterCallback mCallback;
    private String mSelectedValue;

    public interface ListPickerAdapterCallback {
        void onItemPicked(String value);
    }

    public ListPickerAdapter() {
    }

    public void setValues(@NonNull List<String> values, @Nullable String selectedValue) {
        mValues = values;
        if (selectedValue == null &&
                values.size() > 0) { // Select first value in case selection is null
            mSelectedValue = values.get(0);
            // Send callback with selected value when selected value was sent as null (new expense)
            Timber.i("Selecting first value from list %s", mSelectedValue);
            onSelectionMade(mSelectedValue);
        } else {
            mSelectedValue = selectedValue;
            notifyDataSetChanged();
        }
    }

    public void setSelectedValue(@Nonnull String selectedValue) {
        if (mValues == null) {
            return;
        }
        if (ObjectHelper.contains(mValues, selectedValue)) {
            mSelectedValue = selectedValue;
            notifyDataSetChanged();
        }
    }

    public void setCallback(@NonNull ListPickerAdapterCallback callback) {
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

    private void onSelectionMade(String selectedValue) {
        if (mCallback != null) {
            mSelectedValue = selectedValue;
            mCallback.onItemPicked(mSelectedValue);
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

        private void bind(final String value) {
            txtValue.setText(value);
            if (value.equals(mSelectedValue)) {
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

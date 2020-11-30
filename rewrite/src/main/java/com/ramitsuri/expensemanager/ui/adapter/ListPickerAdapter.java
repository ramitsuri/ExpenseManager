package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class ListPickerAdapter extends RecyclerView.Adapter<ListPickerAdapter.ViewHolder> {

    @Nullable
    private List<? extends ListEqualizer> mValues;
    @Nullable
    private ListPickerAdapterCallback mCallback;
    @Nullable
    private ListEqualizer mSelectedValue;

    public interface ListPickerAdapterCallback {
        void onItemPicked(@Nonnull ListEqualizer value);
    }

    public ListPickerAdapter() {
    }

    public <T extends ListEqualizer> void setValues(@NonNull List<T> values,
                                                    @Nullable T selectedValue) {
        mValues = values;
        if ((selectedValue == null || selectedValue.getValue() == null) &&
                values.size() > 0) { // Select first value in case selection is null
            mSelectedValue = values.get(0);
            // Send callback with selected value when selected value was sent as null (new expense)
            Timber.i("Selecting first value from list %s", mSelectedValue);
            onSelectionMade(mSelectedValue);
        } else {
            Timber.i("Selecting supplied value %s", mSelectedValue);
            mSelectedValue = selectedValue;
            notifyDataSetChanged();
        }
    }

    public void setSelectedValue(@Nonnull ListEqualizer selectedValue) {
        if (mValues == null) {
            return;
        }
        if (ObjectHelper.indexOf(mValues, selectedValue.getValue()) != -1) {
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

    private void onSelectionMade(ListEqualizer selectedValue) {
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
        private final Chip txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtValue = itemView.findViewById(R.id.value);
            txtValue.setOnClickListener(this);
        }

        private void bind(@Nonnull final ListEqualizer value) {
            txtValue.setText(value.getValue());
            txtValue.setChecked(mSelectedValue != null
                    && value.getValue().equals(mSelectedValue.getValue()));
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

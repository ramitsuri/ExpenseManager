package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanager.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class MonthPickerAdapter extends RecyclerView.Adapter<MonthPickerAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    @Nullable
    private MonthPickerAdapterCallback mCallback;

    public interface MonthPickerAdapterCallback {
        void onValuePicked(String value);
    }

    public void setValues(@NonNull List<String> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    public void setCallback(@NonNull MonthPickerAdapterCallback callback) {
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
            mCallback.onValuePicked(selectedValue);
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
            txtValue.setChecked(false);
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

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

public class ListPickerMultiSelectionAdapter
        extends RecyclerView.Adapter<ListPickerMultiSelectionAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    @Nullable
    private ListPickerMultiSelectionCallback mCallback;

    public interface ListPickerMultiSelectionCallback {
        void onItemsChanged(List<String> values);
    }

    public void setCallback(@NonNull ListPickerMultiSelectionCallback callback) {
        mCallback = callback;
    }

    public void setValues(@NonNull List<String> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    @Nullable
    public List<String> getValues() {
        return mValues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_picker_multi_selection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mValues != null) {
            String value = mValues.get(position);
            if (value == null) {
                return;
            }
            holder.bind(value);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private Chip txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtValue = itemView.findViewById(R.id.value);
            txtValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtValue.setChecked(false);
                }
            });
            txtValue.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        onDeleted(getAdapterPosition());
                    } else {
                        Timber.w("getAdapterPosition returned -1");
                    }
                }
            });
        }

        private void bind(final String value) {
            txtValue.setText(value);
        }
    }

    private void onDeleted(int deletionIndex) {
        if (mValues != null) {
            mValues.remove(deletionIndex);
            if (mCallback != null) {
                mCallback.onItemsChanged(getValues());
            } else {
                Timber.w("mCallback is null");
            }
        } else {
            Timber.w("mValues is null");
        }
    }
}

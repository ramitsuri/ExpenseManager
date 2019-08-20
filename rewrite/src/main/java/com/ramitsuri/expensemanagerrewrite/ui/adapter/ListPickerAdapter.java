package com.ramitsuri.expensemanagerrewrite.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanagerrewrite.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListPickerAdapter extends RecyclerView.Adapter<ListPickerAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    @Nullable
    private ListPickerAdapterCallback mCallback;
    private int mSelectedPosition;

    public interface ListPickerAdapterCallback {
        void onItemPicked(String value);
    }

    public ListPickerAdapter() {
        mSelectedPosition = 0;
    }

    public void setValues(@NonNull List<String> values) {
        mValues = values;
        notifyDataSetChanged();
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
            txtValue.setChecked(mSelectedPosition == getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mSelectedPosition = getAdapterPosition();
                if (mCallback != null) {
                    if (mValues != null) {
                        mCallback.onItemPicked(mValues.get(mSelectedPosition));
                    } else {
                        Timber.w("mValues is null");
                    }
                } else {
                    Timber.w("mCallback is null");
                }
                notifyDataSetChanged();
            } else {
                Timber.w("getAdapterPosition returned -1");
            }
        }
    }
}

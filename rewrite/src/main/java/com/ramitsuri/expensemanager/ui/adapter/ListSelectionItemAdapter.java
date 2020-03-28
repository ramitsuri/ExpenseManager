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

public class ListSelectionItemAdapter
        extends RecyclerView.Adapter<ListSelectionItemAdapter.ViewHolder> {

    @Nullable
    private List<BudgetCategoryWrapper> mValues;
    @Nullable
    private ListSelectionItemCallback mCallback;

    public interface ListSelectionItemCallback {
        void onItemSelected(BudgetCategoryWrapper value);

        void onItemUnselected(BudgetCategoryWrapper value);
    }

    public ListSelectionItemAdapter() {
    }

    public void setValues(@NonNull List<BudgetCategoryWrapper> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    public void setCallback(@NonNull ListSelectionItemCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_selection_item_item, parent, false);
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

        private void bind(final BudgetCategoryWrapper value) {
            txtValue.setText(value.getCategory());
            txtValue.setEnabled(value.isAvailable());
            txtValue.setChecked(value.isSelected());
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                if (mValues != null) {
                    if (mCallback != null) {
                        BudgetCategoryWrapper item = mValues.get(getAdapterPosition());
                        if (item.isAvailable()) {
                            if (item.isSelected()) { // Unselected
                                mCallback.onItemUnselected(mValues.get(getAdapterPosition()));
                            } else { // Selected
                                mCallback.onItemSelected(mValues.get(getAdapterPosition()));
                            }
                        } else {
                            Timber.i("Unavailable item was selected");
                        }
                    } else {
                        Timber.w("mCallbacks is null");
                    }
                } else {
                    Timber.w("mValues or mUsedValues is null");
                }
            } else {
                Timber.w("getAdapterPosition returned -1");
            }
        }
    }
}

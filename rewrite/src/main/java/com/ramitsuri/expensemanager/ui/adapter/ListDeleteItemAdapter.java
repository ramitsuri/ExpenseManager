package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.ramitsuri.expensemanager.R;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListDeleteItemAdapter
        extends RecyclerView.Adapter<ListDeleteItemAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    private boolean mShowDelete;
    @Nullable
    private ListDeleteItemCallback mCallback;

    public interface ListDeleteItemCallback {
        void onItemClicked(@Nonnull String value);

        void onItemDeleteRequested(@Nonnull String value);
    }

    public void setCallback(@NonNull ListDeleteItemCallback callback) {
        mCallback = callback;
    }

    public void setShowDelete(boolean showDelete) {
        mShowDelete = showDelete;
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
                .inflate(R.layout.list_delete_item_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mValues != null) {
            String value = mValues.get(position);
            if (value == null) {
                return;
            }
            holder.bind(value, mShowDelete);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Chip txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtValue = itemView.findViewById(R.id.value);
            txtValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtValue.setChecked(false);
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mValues != null) {
                            if (mCallback != null) {
                                mCallback.onItemClicked(mValues.get(getAdapterPosition()));
                            } else {
                                Timber.i("mCallbacks is null");
                            }
                        } else {
                            Timber.i("mValues is null");
                        }
                    } else {
                        Timber.w("getAdapterPosition returned -1");
                    }
                }
            });
            txtValue.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mValues != null) {
                            if (mCallback != null) {
                                mCallback.onItemDeleteRequested(mValues.get(getAdapterPosition()));
                            } else {
                                Timber.i("mCallbacks is null");
                            }
                        } else {
                            Timber.i("mValues is null");
                        }
                    } else {
                        Timber.w("getAdapterPosition returned -1");
                    }
                }
            });
        }

        private void bind(final String value, boolean showDelete) {
            txtValue.setText(value);
            if (showDelete) {
                txtValue.setCloseIconVisible(true);
            } else {
                txtValue.setCloseIconVisible(false);
            }
        }
    }
}

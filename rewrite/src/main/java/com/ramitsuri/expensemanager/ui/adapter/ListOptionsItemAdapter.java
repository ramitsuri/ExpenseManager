package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListOptionsItemAdapter
        extends RecyclerView.Adapter<ListOptionsItemAdapter.ViewHolder> {

    @Nullable
    private List<String> mValues;
    @Nullable
    private ListOptionsItemCallback mCallback;

    public interface ListOptionsItemCallback {
        void onItemDeleteRequested(@Nonnull String value);

        void onItemEditRequested(@Nonnull String value);
    }

    public void setCallback(@NonNull ListOptionsItemCallback callback) {
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
                .inflate(R.layout.list_options_item_item, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtValue = itemView.findViewById(R.id.value);
            Button btnEdit = itemView.findViewById(R.id.btn_edit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mValues != null) {
                            if (mCallback != null) {
                                mCallback.onItemEditRequested(mValues.get(getAdapterPosition()));
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

            Button btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
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

        private void bind(final String value) {
            txtValue.setText(value);
        }
    }
}

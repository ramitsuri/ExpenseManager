package com.ramitsuri.expensemanagerrewrite.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerrewrite.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListPickerAdapter extends RecyclerView.Adapter<ListPickerAdapter.ViewHolder> {

    @NonNull
    private List<String> mValues;
    @NonNull
    private ListPickerAdapterCallback mCallback;

    public interface ListPickerAdapterCallback {
        void onItemPicked(String value);
    }

    public ListPickerAdapter(@NonNull List<String> values,
            @NonNull ListPickerAdapterCallback callback) {
        mValues = values;
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
        holder.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ViewGroup container;
        private TextView txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);

            txtValue = itemView.findViewById(R.id.value);
        }

        private void bind(final String value) {
            txtValue.setText(value);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mCallback.onItemPicked(mValues.get(getAdapterPosition()));
            } else {
                Timber.e("getAdapterPosition returned -1");
            }
        }
    }
}

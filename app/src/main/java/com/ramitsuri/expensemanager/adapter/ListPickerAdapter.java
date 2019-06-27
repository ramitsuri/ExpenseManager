package com.ramitsuri.expensemanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;

import java.util.List;

public class ListPickerAdapter<T> extends RecyclerView.Adapter<ListPickerAdapter.CustomViewHolder> {

    private List<T> mValues;
    private ListPickerAdapterCallbacks<T> mCallbacks;
    private T mSelectedItem;
    private Context mContext;

    public interface ListPickerAdapterCallbacks<T> {
        void onItemSelected(T item);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup mContainer;
        private TextView mValue;

        public CustomViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.container);
            mValue = (TextView)view.findViewById(R.id.value);
            mContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == mContainer) {
                mCallbacks.onItemSelected(mValues.get(getAdapterPosition()));
            }
        }
    }

    public ListPickerAdapter(Fragment fragment, List<T> values, T selectedItem) {
        mContext = fragment.getContext();
        mValues = values;
        mCallbacks = (ListPickerAdapterCallbacks)fragment;
        mSelectedItem = selectedItem;
    }

    @Override
    public ListPickerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_row, null);
        ListPickerAdapter.CustomViewHolder viewHolder =
                new ListPickerAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListPickerAdapter.CustomViewHolder holder, int position) {
        holder.mValue.setText(mValues.get(position).toString());
        if (mValues.get(position).equals(mSelectedItem)) {
            holder.mValue.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

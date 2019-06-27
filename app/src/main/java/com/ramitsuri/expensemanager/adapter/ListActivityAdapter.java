package com.ramitsuri.expensemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;

import java.util.List;

public class ListActivityAdapter<T>
        extends RecyclerView.Adapter<ListActivityAdapter.CustomViewHolder> {

    private List<T> mValues;
    private ListActivityAdapterCallbacks<T> mCallbacks;
    private Context mContext;

    public interface ListActivityAdapterCallbacks<T> {
        void onItemClicked(T item);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mValue;

        public CustomViewHolder(View itemView) {
            super(itemView);
            setupViews();
        }

        private void setupViews() {
            mValue = (TextView)itemView.findViewById(R.id.value);
            mValue.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == mValue) {
                mCallbacks.onItemClicked(mValues.get(getAdapterPosition()));
            }
        }
    }

    public ListActivityAdapter(Context context, List<T> values) {
        mValues = values;
        mContext = context;
        mCallbacks = (ListActivityAdapterCallbacks)context;
    }

    @Override
    public ListActivityAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ListActivityAdapter.CustomViewHolder holder, int position) {
        holder.mValue.setText(mValues.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

package com.ramitsuri.expensemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>{

    private List<String> mValues;

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mValue;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mValue = (TextView)itemView.findViewById(R.id.value);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public RecyclerViewAdapter(List<String> values){
        mValues = values;
    }

    @Override
    public RecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CustomViewHolder holder, int position) {
            holder.mValue.setText(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

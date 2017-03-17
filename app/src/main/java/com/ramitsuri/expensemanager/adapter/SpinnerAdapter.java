package com.ramitsuri.expensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;

import java.util.List;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    private Context mContext;
    private List<T> mList;

    public SpinnerAdapter(Context context, int resource, List<T> values) {
        super(context, resource, values);
        mContext = context;
        mList = values;
    }

    public int getCount(){
        return mList.size();
    }

    public T getItem(int position){
        return mList.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View  view = inflater.inflate(R.layout.simple_row, parent, false);
        TextView value = (TextView)view.findViewById(R.id.value);
        value.setText((mList.get(position)).toString());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View  view = inflater.inflate(R.layout.simple_row, parent, false);
        TextView value = (TextView)view.findViewById(R.id.value);
        value.setText((mList.get(position)).toString());
        return view;
    }
}

package com.ramitsuri.expensemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.helper.ActivityHelper;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>{

    private List<String> mValues;
    private RecyclerView mRecyclerView;
    private int mPreviousPosition;

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mValue;
        private EditText mValueEdit;
        private ImageView mBtnSaveValue;
        private LinearLayout mValueEditContainer;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mValue = (TextView)itemView.findViewById(R.id.value);
            mValueEdit = (EditText)itemView.findViewById(R.id.value_edit);
            mBtnSaveValue = (ImageView)itemView.findViewById(R.id.btn_save_value);
            mValueEditContainer = (LinearLayout)itemView.findViewById(R.id.edit_container);
            mValue.setOnClickListener(this);
            mBtnSaveValue.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == mValue){
                View previousView = mRecyclerView.getChildAt(mPreviousPosition);
                if(previousView!=null) {
                    previousView.findViewById(R.id.edit_container).setVisibility(View.GONE);
                    ((TextView) previousView.findViewById(R.id.value)).setText(
                            ((EditText) previousView.findViewById(R.id.value_edit)).getEditableText().toString());
                    previousView.findViewById(R.id.value).setVisibility(View.VISIBLE);
                }
                mValueEditContainer.setVisibility(View.VISIBLE);
                mValue.setVisibility(View.GONE);
                mValueEdit.requestFocus();
                ActivityHelper.showSoftKeyboard(mRecyclerView.getContext(), mValueEdit);
                mPreviousPosition = getAdapterPosition();
            } else if (view == mBtnSaveValue){
                mValueEditContainer.setVisibility(View.GONE);
                mValue.setText(mValueEdit.getEditableText().toString());
                mValue.setVisibility(View.VISIBLE);
                mValueEdit.clearFocus();
                ActivityHelper.hideSoftKeyboard(mRecyclerView.getContext(), mValueEdit);
            }
        }
    }

    public RecyclerViewAdapter(List<String> values){
        mValues = values;
    }

    @Override
    public RecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_row, null);
        mRecyclerView = (RecyclerView)parent;
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CustomViewHolder holder, int position) {
        holder.mValue.setText(mValues.get(position));
        holder.mValueEdit.setText(mValues.get(position));
        holder.mValueEdit.setSelection(mValues.get(position).length());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

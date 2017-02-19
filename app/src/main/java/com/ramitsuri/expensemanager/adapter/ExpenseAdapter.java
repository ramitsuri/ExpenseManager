package com.ramitsuri.expensemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.ArrayList;

/**
 * Created by ramitsuri on 1/29/2017.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.CustomViewHolder>   {

    private ArrayList<Expense> mExpenses;

    private static final int mItemViewTypeTop = 0;

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mFieldDate;
        private TextView mFieldCategory;
        private TextView mFieldPaymentMode;
        private TextView mFieldDescription;
        private TextView mFieldAmount;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mFieldDate = (TextView)itemView.findViewById(R.id.expense_date);
            mFieldCategory = (TextView)itemView.findViewById(R.id.expense_category);
            mFieldPaymentMode = (TextView)itemView.findViewById(R.id.expense_payment_mode);
            mFieldDescription = (TextView)itemView.findViewById(R.id.expense_description);
            mFieldAmount = (TextView)itemView.findViewById(R.id.expense_amount);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public ExpenseAdapter(ArrayList<Expense> expenses){
        mExpenses = expenses;
    }

    @Override
    public ExpenseAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomViewHolder viewHolder;
        View view;
        switch (viewType){

            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row_top, null);
                viewHolder = new CustomViewHolder(view);
                break;

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, null);
                viewHolder = new CustomViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpenseAdapter.CustomViewHolder holder, int position) {
        //holder.mFieldDate.setText(String.valueOf(mExpenses.get(position).getDateTime()));
        if(position>0) {
            holder.mFieldCategory.setText(mExpenses.get(position).getCategory().getName());
            holder.mFieldPaymentMode.setText(mExpenses.get(position).getPaymentMode());
            holder.mFieldDescription.setText(mExpenses.get(position).getDescription());
            //holder.mFieldAmount.setText(String.valueOf(mExpenses.get(position).getAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return mItemViewTypeTop;
        }
        return 1;
    }
}

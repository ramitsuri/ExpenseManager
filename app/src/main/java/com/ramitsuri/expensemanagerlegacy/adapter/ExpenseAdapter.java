package com.ramitsuri.expensemanagerlegacy.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.entities.Expense;
import com.ramitsuri.expensemanagerlegacy.helper.DateHelper;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.CustomViewHolder> {

    private List<Expense> mExpenses;
    private Context mContext;

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mFieldDate, mFieldCategory, mFieldPaymentMethod, mFieldDescription,
                mFieldAmount, mStore, mStoreDivider;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mFieldDate = (TextView)itemView.findViewById(R.id.expense_date);
            mFieldCategory = (TextView)itemView.findViewById(R.id.expense_category);
            mFieldPaymentMethod = (TextView)itemView.findViewById(R.id.expense_payment_mode);
            mFieldDescription = (TextView)itemView.findViewById(R.id.expense_description);
            mFieldAmount = (TextView)itemView.findViewById(R.id.expense_amount);
            mStore = (TextView)itemView.findViewById(R.id.expense_store);
            mStoreDivider = (TextView)itemView.findViewById(R.id.expense_text_divider2);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public ExpenseAdapter(List<Expense> expenses, Context context) {
        mContext = context;
        mExpenses = expenses;
    }

    public ExpenseAdapter(List<Expense> expenses) {
        mExpenses = expenses;
    }

    @Override
    public ExpenseAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpenseAdapter.CustomViewHolder holder, int position) {
        holder.mFieldCategory.setText(mExpenses.get(position).getCategory().getName());
        holder.mFieldPaymentMethod.setText(mExpenses.get(position).getPaymentMethod().getName());
        holder.mFieldDescription.setText(mExpenses.get(position).getDescription());
        holder.mFieldAmount.setText(String.valueOf(mExpenses.get(position).getAmount()));
        holder.mFieldDate.setText(DateHelper.
                getJustTheDayOfMonth(mExpenses.get(position).getDateTime()));
        if (mExpenses.get(position).getStore().equals("<EMPTY>") ||
                mExpenses.get(position).getStore().isEmpty()) {
            holder.mStoreDivider.setVisibility(View.GONE);
            holder.mStore.setVisibility(View.GONE);
        } else {
            holder.mStore.setText(mExpenses.get(position).getStore());
        }

        if (mExpenses.get(position).isSynced()) {
            holder.mFieldAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            holder.mFieldAmount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }
}

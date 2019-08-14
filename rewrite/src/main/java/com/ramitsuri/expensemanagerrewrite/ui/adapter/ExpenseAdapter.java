package com.ramitsuri.expensemanagerrewrite.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> mExpenses;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtDate, txtCategory, txtPaymentMethod, txtDescription, txtAmount,
                txtStore, txtStoreDivider;

        ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_expense_date);
            txtCategory = itemView.findViewById(R.id.txt_expense_category);
            txtPaymentMethod = itemView.findViewById(R.id.txt_expense_payment_method);
            txtDescription = itemView.findViewById(R.id.txt_expense_description);
            txtAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtStore = itemView.findViewById(R.id.txt_expense_store);
            txtStoreDivider = itemView.findViewById(R.id.txt_expense_divider2);
        }

        private void bind(final Expense expense) {
            txtCategory.setText(String.valueOf(expense.getCategory().charAt(0)));
            txtPaymentMethod.setText(expense.getPaymentMethod());
            txtDescription.setText(expense.getDescription());
            txtAmount.setText(String.valueOf(expense.getAmount()));
        /*txtDate.setText(DateHelper.
                getJustTheDayOfMonth(expense.getDateTime()));*/
            txtDate.setText("Aug 7th");
            if (TextUtils.isEmpty(expense.getStore())) {
                txtStoreDivider.setVisibility(View.GONE);
                txtStore.setVisibility(View.GONE);
            } else {
                txtStore.setText(expense.getStore());
            }

       /* if (expense.isSynced()) {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }*/
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void setExpenses(List<Expense> expenses) {
        mExpenses = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = mExpenses.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }
}
